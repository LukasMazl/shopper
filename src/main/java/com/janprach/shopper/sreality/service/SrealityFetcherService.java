package com.janprach.shopper.sreality.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.janprach.shopper.sreality.api.Estate.Geo;
import com.janprach.shopper.sreality.api.EstateListing.EstateListingEmbedded.EstateSummary;
import com.janprach.shopper.sreality.entity.Estate;
import com.janprach.shopper.sreality.entity.Image;
import com.janprach.shopper.sreality.entity.RawResponse;
import com.janprach.shopper.sreality.service.RichHttpClient.EntityWithRawResponse;
import com.janprach.shopper.sreality.util.CoordinateUtils;
import com.janprach.shopper.sreality.util.CoordinateUtils.Coordinates;

import lombok.AllArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
@Component
public class SrealityFetcherService {
	private final EstateFetcherService estateFetcherService;
	private final EstateService estateService;
	private final ImageFetcherService imageFetcherService;
	private final SrealityUrlTranslationService srealityUrlTranslationService;

	// @Scheduled(cron = "${com.janprach.shopper.sreality.cron}")
	@Scheduled(initialDelayString = "${com.janprach.shopper.sreality.initialDelay}", fixedDelayString = "${com.janprach.shopper.sreality.fixedDelay}")
	public void fetchAndStoreLatestRealites() {
		try {
			int countSaved = 0;
			HashSet<Long> srealityIds = new HashSet<>();
			Iterator<EstateSummary> iterator = this.estateFetcherService.fetchEstateSummaries().iterator();
			while (iterator.hasNext()) {
				EstateSummary estateSummary = iterator.next();
				srealityIds.add(estateSummary.getHashId());
				if (fetchAndStoreEstateSummary(estateSummary).isPresent()) {
					countSaved++;
				}
			}
			log.info("Saved {} estates.", countSaved);

			int countDeleted = this.estateService.updateInactive(srealityIds);
			log.info("Deleted {} estates.", countDeleted);
		} catch (Exception e) {
			log.error("Error fetching and storing estates.", e);
		}
	}

	private Optional<Estate> fetchAndStoreEstateSummary(final EstateSummary estateSummary) {
		val estateHashId = estateSummary.getHashId();
		val estateOld = this.estateService.findBySrealityId(estateHashId);
		if (estateOld == null) {
			// new
			val estateNew = fetchEstate(estateHashId);
			estateNew.ifPresent(estate -> {
				for (val image : estate.getImages()) {
					val imageMetaDataOption = imageFetcherService.fetchAndSaveImage(image.getUrl());
					imageMetaDataOption.ifPresent(imageMetaData -> {
						image.setSha1(imageMetaData.getSha1());
						image.setWidth(imageMetaData.getWidth());
						image.setHeight(imageMetaData.getHeight());
					});
				}
				estateService.convertAndInsert(estate);
			});
			return estateNew;
		} else {
			if (estateEquals(estateOld, estateSummary)) {
				// not changed
				return Optional.empty();
			} else {
				// changed
				val estateNew = fetchEstate(estateHashId);
				estateNew.ifPresent(e -> estateService.convertAndUpdate(estateOld, e));
				return estateNew;
			}
		}
	}

	private boolean estateEquals(final Estate estateOld, final EstateSummary estateSummary) {
		return estateOld.getActive()
				&& Objects.equals(estateOld.getSrealityId(), estateSummary.getHashId())
				&& Objects.equals(estateOld.getPrice(), estateSummary.getPrice());
	}

	private Optional<Estate> fetchEstate(final long estateHashId) {
		val estateWithRawResponse = this.estateFetcherService.fetchEstateWithRawResponse(estateHashId);
		return estateWithRawResponse.map(ewrr -> this.parseEstate(estateHashId, ewrr));
	}

	private Estate parseEstate(final long estateHashId,
			final EntityWithRawResponse<com.janprach.shopper.sreality.api.Estate> estateWithRawResponse) {
		val estateSreality = estateWithRawResponse.getValue();
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

		val imagesSreality = estateSreality.getEmbedded().getImages();
		val images = imagesSreality.stream().filter(i -> i != null && i.getLinks() != null).flatMap(image -> {
			val srealityId = image.getId();
			val links = image.getLinks();
			val linkStream = Stream.of(links.getSelf(), links.getGallery(), links.getView()).filter(l -> l != null);
			return linkStream.map(l -> new Image(l.getTitle(), estateEntity, srealityId, l.getHref(), null, null, null));
		}).collect(Collectors.toList());
		estateEntity.setImages(images);

		val rawResponse = new RawResponse(estateEntity, estateWithRawResponse.getRawResponse());
		estateEntity.getRawResponses().add(rawResponse);

		return estateEntity;
	}

	private Integer parseInteger(final Map<String, Object> map, final String key) {
		val value = map.get(key);
		return value == null ? null : Integer.parseInt(value.toString());
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
