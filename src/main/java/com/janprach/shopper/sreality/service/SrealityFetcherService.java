package com.janprach.shopper.sreality.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.codepoetics.protonpack.StreamUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.janprach.shopper.config.SrealityFetcherConfig;
import com.janprach.shopper.sreality.api.EstateListing;
import com.janprach.shopper.sreality.api.EstateListing.EstateListingEmbedded.EstateSummary;
import com.janprach.shopper.sreality.entity.Estate;
import com.janprach.shopper.sreality.entity.Image;
import com.janprach.shopper.sreality.entity.RawResponse;
import com.janprach.shopper.sreality.util.CoordinateUtils;

@Slf4j
@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
@Component
public class SrealityFetcherService {
	private static final int CONSECUTIVE_UNCHANGED_BEFORE_BREAK = 10;
	private static final int ESTATE_SUMMARIES_PER_PAGE = 20;

	private final Client client;
	private final EstateService estateService;
	private final ObjectMapper objectMapper;
	private final SrealityFetcherConfig srealityFetcherConfig;
	private final SrealityUrlTranslationService srealityUrlTranslationService;

	// @Scheduled(cron = "${com.janprach.shopper.sreality.cron}")
	@Scheduled(initialDelayString = "${com.janprach.shopper.sreality.initialDelay}", fixedDelayString = "${com.janprach.shopper.sreality.fixedDelay}")
	public void fetchAndStoreLatestRealites() {
		val iterator = this.fetchEstates().iterator();
		int consecutiveSameEstateCounter = 0;
		while (iterator.hasNext() && consecutiveSameEstateCounter < CONSECUTIVE_UNCHANGED_BEFORE_BREAK) {
			val estate = iterator.next();
			val previousEstate = this.estateService.findBySrealityId(estate.getSrealityId());
			if (previousEstate == null) {
				this.estateService.saveEstate(estate);
				consecutiveSameEstateCounter = 0;
			} else {
				if (estateEquals(previousEstate, estate)) {
					log.info("Unchanged entity {}.", estate.getSrealityId());
					consecutiveSameEstateCounter++;
				} else { // TODO: what will happen on update with old/new images and response?
					estate.setEntityBaseFieldsForUpdate(previousEstate);
					this.estateService.saveEstate(estate);
					consecutiveSameEstateCounter = 0;
				}
			}
		}
	}

	Stream<Estate> fetchEstates() {
		val estates = this.fetchEstateSummaries().map(estateSummary -> {
			final Long estateHashId = estateSummary.getHashId();
			try {
				final Response estateResponse = this.fetchEstate(estateHashId);
				final String estateResponseString = estateResponse.readEntity(String.class);
				final Estate estate = this.parseEstate(estateHashId, estateResponseString);
				return Optional.of(estate);
			} catch (final Exception e) {
				log.error("Failed fetching or parsing estate id {}.", estateHashId, e);
				return Optional.<Estate> empty();
			}
		});
		return StreamUtils.takeWhile(estates, e -> e.isPresent()).map(e -> e.get());
	}

	Stream<EstateSummary> fetchEstateSummaries() {
		val infiniteEstateListings = IntStream.iterate(1, page -> page + 1).boxed()
				.map(page -> this.fetchEstateListing(page, ESTATE_SUMMARIES_PER_PAGE));
		val estateListings = StreamUtils.takeWhile(infiniteEstateListings,
				el -> el.isPresent() && !el.get().getEmbedded().getEstates().isEmpty());
		return estateListings.flatMap(el -> el.get().getEmbedded().getEstates().stream());
	}

	private Optional<EstateListing> fetchEstateListing(final int page, final int perPage) {
		// http://www.sreality.cz/api/cs/v1/estates?category_main_cb=2&category_type_cb=1&locality_region_id=10&per_page=60&page=2
		val sanitizedPage = Math.max(1, page);
		val sanitizedPerPage = Math.max(20, Math.min(60, perPage));
		log.info("Fetching estate listing page {} (x{}) ...", page, perPage);
		try {
			val estateListingRespose = this.fetchSrealityEstate(wt -> {
				wt = this.addParam(wt, "category_main_cb", srealityFetcherConfig.getCategoryMain());
				wt = this.addParam(wt, "category_sub_cb", srealityFetcherConfig.getCategorySub());
				wt = this.addParam(wt, "category_type_cb", srealityFetcherConfig.getCategoryType());
				wt = this.addParam(wt, "locality_district_id", srealityFetcherConfig.getLocalityDistrict());
				wt = this.addParam(wt, "locality_region_id", srealityFetcherConfig.getLocalityRegion());
				wt = this.addParam(wt, "czk_price_summary_order2", srealityFetcherConfig.getPriceRange());
				wt = wt.queryParam("per_page", Integer.toString(sanitizedPerPage));
				wt = wt.queryParam("page", Integer.toString(sanitizedPage));
				return wt;
			});
			val estateListing = estateListingRespose.readEntity(com.janprach.shopper.sreality.api.EstateListing.class);
			return Optional.of(estateListing);
		} catch (final Exception e) {
			log.error("Failed fetching or parsing estate listing page {} (x{}).", page, perPage, e);
			return Optional.<EstateListing> empty();
		}
	}

	private Response fetchEstate(final long estateHashId) {
		// http://www.sreality.cz/api/cs/v1/estates/3410497628
		log.info("Fetching estate id {} ...", estateHashId);
		return this.fetchSrealityEstate(wt -> wt.path(Long.toString(estateHashId)));
	}

	private Response fetchSrealityEstate(final Function<WebTarget, WebTarget> webTargetModifier) {
		val estatesWebTarget = this.client.target("http://www.sreality.cz").path("/api/cs/v1/estates");
		val webTarget = webTargetModifier.apply(estatesWebTarget);
		log.debug("Fetching {}", webTarget.getUri());
		return webTarget.request(MediaType.APPLICATION_JSON_TYPE)
				.header("User-Agent", this.getClass().getSimpleName() + " v0.0.1").get();
	}

	private Estate parseEstate(final long estateHashId, final String estateResponseString) throws IOException {
		val estate = this.objectMapper.readValue(estateResponseString, com.janprach.shopper.sreality.api.Estate.class);
		val items = estate.getItems().stream().collect(Collectors.toMap(i -> i.getName(), i -> i.getValue()));
		val wgs84 = CoordinateUtils.seznamCzPpxPpy2CoordinateWGS84(estate.getMap().getPpx(), estate.getMap().getPpy());

		val areaBuild = this.parseInteger(items, "Plocha zastavěná");
		val areaFloor = this.parseInteger(items, "Plocha podlahová");
		val areaGarden = this.parseInteger(items, "Plocha zahrady");
		val areaTotal = this.parseInteger(items, "Plocha pozemku");
		val areaUsable = this.parseInteger(items, "Užitná plocha");
		val address = estate.getLocality().getValue();
		val description = estate.getText().getValue();
		val metaDescription = estate.getMetaDescription();
		val price = estate.getPriceCzk().getValueRaw();
		val estateSrealityId = estateHashId;
		val state = items.get("Stav objektu").toString();
		val title = estate.getName().getValue();
		val estateUrl = this.srealityUrlTranslationService.getUrlString(estateHashId, estate.getSeo());
		val zoom = estate.getMap().getZoom();
		val estateEntity = new Estate(areaBuild, areaFloor, areaGarden, areaTotal, areaUsable, address, description,
				wgs84.getLatitude(), wgs84.getLongitude(), metaDescription, price, estateSrealityId, state, title,
				estateUrl, zoom, new ArrayList<Image>(), new ArrayList<RawResponse>());

		val images = estate.getEmbedded().getImages().stream().map(image -> {
			final String imageDescription = image.getLinks().getSelf().getTitle();
			final Long imageSrealityId = image.getId();
			final String imageUrl = image.getLinks().getSelf().getHref();
			return new Image(imageDescription, estateEntity, imageSrealityId, imageUrl);
		}).collect(Collectors.toList());
		estateEntity.setImages(images);

		val rawResponse = new RawResponse(estateEntity, estateResponseString);
		estateEntity.getRawResponses().add(rawResponse);

		return estateEntity;
	}

	private Integer parseInteger(final Map<String, Object> map, final String key) {
		val value = map.get(key);
		return value == null ? null : Integer.parseInt(value.toString());
	}

	private boolean estateEquals(final Estate a, final Estate b) {
		return Objects.equal(a.getAddress(), b.getAddress())
				&& Objects.equal(a.getDescription(), b.getDescription())
				&& Objects.equal(a.getLatitude(), b.getLatitude())
				&& Objects.equal(a.getLongitude(), b.getLongitude())
				&& Objects.equal(a.getPrice(), b.getPrice())
				&& Objects.equal(a.getSrealityId(), b.getSrealityId())
				&& Objects.equal(a.getState(), b.getState())
				&& Objects.equal(a.getTitle(), b.getTitle());
	}

	private WebTarget addParam(final WebTarget webTarget, final String name, final String value) {
		if (value == null) {
			return webTarget;
		} else {
			return webTarget.queryParam(name, value);
		}
	}
}
