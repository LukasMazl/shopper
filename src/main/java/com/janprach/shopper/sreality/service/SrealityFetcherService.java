package com.janprach.shopper.sreality.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
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
import com.janprach.shopper.sreality.api.Estate.Geo;
import com.janprach.shopper.sreality.api.EstateListing;
import com.janprach.shopper.sreality.api.EstateListing.EstateListingEmbedded.EstateSummary;
import com.janprach.shopper.sreality.entity.Estate;
import com.janprach.shopper.sreality.entity.Image;
import com.janprach.shopper.sreality.entity.RawResponse;
import com.janprach.shopper.sreality.util.CoordinateUtils;
import com.janprach.shopper.sreality.util.CoordinateUtils.Coordinates;

@Slf4j
@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
@Component
public class SrealityFetcherService {
	private static final int ESTATE_SUMMARIES_PER_PAGE = 20;
	private static final int MAX_RETRIES = 3;

	private final Client client;
	private final EstateService estateService;
	private final ObjectMapper objectMapper;
	private final SrealityFetcherConfig srealityFetcherConfig;
	private final SrealityUrlTranslationService srealityUrlTranslationService;

	// @Scheduled(cron = "${com.janprach.shopper.sreality.cron}")
	@Scheduled(initialDelayString = "${com.janprach.shopper.sreality.initialDelay}", fixedDelayString = "${com.janprach.shopper.sreality.fixedDelay}")
	public void fetchAndStoreLatestRealites() {
		int countSaved = 0;
		HashSet<Long> srealityIds = new HashSet<>();
		Iterator<EstateSummary> iterator = fetchEstateSummaries().iterator();
		while (iterator.hasNext()) {
			EstateSummary estateSummary = iterator.next();
			srealityIds.add(estateSummary.getHashId());
			if (fetchAndStoreEstateSummary(estateSummary)) {
				countSaved++;
			}
		}
		log.info("Saved " + countSaved + " estates");

		int countDeleted = this.estateService.updateInactive(srealityIds);
		log.info("Deleted " + countDeleted + " estates");
	}

	private boolean fetchAndStoreEstateSummary(EstateSummary estateSummary) {
		Estate estateOld = this.estateService.findBySrealityId(estateSummary.getHashId());
		if (estateOld == null) {
			// new
			Estate estateNew = fetchEstate(estateSummary);
			if (estateNew == null) {
				return false;
			}
			return estateService.convertAndInsert(estateNew);
		} else {
			if (estateEquals(estateOld, estateSummary)) {
				// not changed
				return false;
			} else {
				// changed
				Estate estateNew = fetchEstate(estateSummary);
				if (estateNew == null) {
					return false;
				}
				return estateService.convertAndUpdate(estateOld, estateNew);
			}
		}
	}

	private boolean estateEquals(final Estate estateOld, final EstateSummary estateSummary) {
		return estateOld.getActive()
				&& Objects.equal(estateOld.getSrealityId(), estateSummary.getHashId())
				&& Objects.equal(estateOld.getPrice(), estateSummary.getPrice());
	}

	private Estate fetchEstate(EstateSummary estateSummary) {
		val estateHashId = estateSummary.getHashId();
		try {
			val estateResponse = this.fetchEstate(estateHashId);
			val estateResponseString = estateResponse.readEntity(String.class);
			val estate = this.parseEstate(estateHashId, estateResponseString);
			return estate;
		} catch (final Exception e) {
			log.error("Failed fetching or parsing estate id {}.", estateHashId, e);
			return null;
		}
	}

	Stream<EstateSummary> fetchEstateSummaries() {
		val infiniteEstateListings = IntStream.iterate(1, page -> page + 1).boxed()
				.map(page -> this.fetchEstateListing(page, ESTATE_SUMMARIES_PER_PAGE));
		val estateListings = StreamUtils.takeWhile(infiniteEstateListings,
				el -> el.isPresent() && !el.get().getEmbedded().getEstates().isEmpty());
		return estateListings.flatMap(el -> el.get().getEmbedded().getEstates().stream());
	}

	private Optional<EstateListing> fetchEstateListing(final int page, final int perPage) {
		if (page > 60)
			return null;
		
		// http://www.sreality.cz/api/cs/v1/estates?category_main_cb=2&category_type_cb=1&locality_region_id=10&per_page=60&page=2
		val sanitizedPage = Math.max(1, page);
		val sanitizedPerPage = Math.max(20, Math.min(60, perPage));
		log.debug("Fetching estate listing page {} (x{}) ...", page, perPage);
		try {
			Thread.sleep(1000L + (long) (Math.random() * 3000L));
			for (int retries = 0; retries < MAX_RETRIES; retries++) {
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
				val statusFamily = estateListingRespose.getStatusInfo().getFamily();
				if (statusFamily == Response.Status.Family.SERVER_ERROR && retries < MAX_RETRIES) {
					val sleepTime = 60 * (1 << retries);
					val responseString = estateListingRespose.readEntity(String.class);
					log.warn("Server error (http status 5xx). Retry in {} s.\n{}", sleepTime, responseString);
					Thread.sleep(1000L * sleepTime);
				} else {
					val estateListing = estateListingRespose.readEntity(EstateListing.class);
					return Optional.of(estateListing);
				}
			}
		} catch (final Exception e) {
			log.error("Failed fetching or parsing estate listing page {} (x{}).", page, perPage, e);
		}
		return Optional.<EstateListing> empty();
	}

	private Response fetchEstate(final long estateHashId) {
		// http://www.sreality.cz/api/cs/v1/estates/3410497628
		log.debug("Fetching estate id {} ...", estateHashId);
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
		val estateSreality = this.objectMapper.readValue(estateResponseString, com.janprach.shopper.sreality.api.Estate.class);
		val items = estateSreality.getItems().stream().collect(Collectors.toMap(i -> i.getName(), i -> i.getValue()));
		val coordinates = this.getCoordinates(estateSreality.getMap());

		Estate estateEntity = new Estate();
		estateEntity.setAreaBuild(this.parseInteger(items, "Plocha zastavěná"));
		estateEntity.setAreaFloor(this.parseInteger(items, "Plocha podlahová"));
		estateEntity.setAreaGarden(this.parseInteger(items, "Plocha zahrady"));
		estateEntity.setAreaTotal(this.parseInteger(items, "Plocha pozemku"));
		estateEntity.setAreaUsable(this.parseInteger(items, "Užitná plocha"));
		estateEntity.setAddress(estateSreality.getLocality().getValue());
		estateEntity.setDescription(estateSreality.getText().getValue());
		estateEntity.setLatitude(coordinates.getLatitude());
		estateEntity.setLongitude(coordinates.getLongitude());
		estateEntity.setMetaDescription(estateSreality.getMetaDescription());
		estateEntity.setPrice(estateSreality.getPriceCzk().getValueRaw());
		estateEntity.setSrealityId(estateHashId);
		estateEntity.setState(items.get("Stav objektu").toString());
		estateEntity.setTitle(estateSreality.getName().getValue());
		estateEntity.setUrl(this.srealityUrlTranslationService.getUrlString(estateHashId, estateSreality.getSeo()));
		estateEntity.setZoom(estateSreality.getMap().getZoom());
		estateEntity.setDateSort(new Date());

		val images = estateSreality.getEmbedded().getImages().stream().map(image -> {
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

	private WebTarget addParam(final WebTarget webTarget, final String name, final String value) {
		if (value == null) {
			return webTarget;
		} else {
			return webTarget.queryParam(name, value);
		}
	}

	private Coordinates getCoordinates(final Geo geo) {
		if (geo.getLat() != null && geo.getLon() != null) {
			return new Coordinates(geo.getLat(), geo.getLon());
		} else if (geo.getPpx() != null && geo.getPpy() != null) {
			return CoordinateUtils.seznamCzPpxPpy2WGS84(geo.getPpx(), geo.getPpy());
		} else {
			return new Coordinates(0.0, 0.0);
		}
	}
}
