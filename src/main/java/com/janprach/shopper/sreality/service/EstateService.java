package com.janprach.shopper.sreality.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.janprach.shopper.sreality.entity.Estate;
import com.janprach.shopper.sreality.entity.History;
import com.janprach.shopper.sreality.entity.HistoryType;
import com.janprach.shopper.sreality.entity.Image;
import com.janprach.shopper.sreality.entity.RawResponse;
import com.janprach.shopper.sreality.repository.EstateRepository;
import com.janprach.shopper.sreality.repository.HistoryRepository;
import com.janprach.shopper.sreality.repository.ImageRepository;
import com.janprach.shopper.sreality.repository.RawResponseRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
@Component("estateService")
public class EstateService {
	@javax.inject.Inject
	public EstateService(final EstateRepository estateRepository, final HistoryRepository historyRepository,
			final ImageRepository imageRepository, final RawResponseRepository rawResponseRepository,
			@Value("${com.janprach.shopper.sreality.priceDiffThreshold}") final int priceDiffThreshold) {
		super();
		this.estateRepository = estateRepository;
		this.historyRepository = historyRepository;
		this.imageRepository = imageRepository;
		this.rawResponseRepository = rawResponseRepository;
		this.priceDiffThreshold = priceDiffThreshold;
	}

	private final EstateRepository estateRepository;
	private final HistoryRepository historyRepository;
	private final ImageRepository imageRepository;
	private final RawResponseRepository rawResponseRepository;
	@Value("${com.janprach.shopper.sreality.priceDiffThreshold}")
	private final int priceDiffThreshold;

	@Transactional(readOnly = true)
	public Estate findBySrealityId(final long srealityId) {
		return this.estateRepository.findBySrealityId(srealityId);
	}

	@Transactional(readOnly = true)
	public List<Estate> findAllActive() {
		return this.estateRepository.findAllByActive(true);
	}

 	@Transactional(readOnly = true)
 	public List<Estate> findDuplicateEstates(final Estate estate) {
 		final String sanitizedAddressLike = StringUtils.substringBefore(estate.getAddress(), ",") + "%";
 		if (estate.getAddress().contains(",")) {
 			List<Estate> duplicatesByAddressAndPriceAndArea = this.estateRepository.findDuplicatesByAddressAndPriceAndArea(
 					sanitizedAddressLike, estate.getPrice(), estate.getAreaTotal(), estate.getAreaUsable());
 			if (!duplicatesByAddressAndPriceAndArea.isEmpty()) {
 				return duplicatesByAddressAndPriceAndArea;
 			}
 		}

 		List<Estate> duplicatesByPriceAndArea = this.estateRepository.findDuplicatesByPriceAndArea(
 				estate.getPrice(), estate.getAreaTotal(), estate.getAreaUsable());
 		if (!duplicatesByPriceAndArea.isEmpty()) {
 			return duplicatesByPriceAndArea;
 		}

 		if (estate.getAddress().contains(",")) {
 			List<Estate> duplicatesByAddressAndArea = this.estateRepository.findDuplicatesByAddressAndArea(
 					sanitizedAddressLike, estate.getAreaTotal(), estate.getAreaUsable());
 			if (!duplicatesByAddressAndArea.isEmpty()) {
 				return duplicatesByAddressAndArea;
 			}
 		}
 		
 		return new ArrayList<Estate>();
 	}

	@Transactional
	public void convertAndInsert(final Estate estateNew) {
		final List<Image> images = new ArrayList<>(estateNew.getImages());
		estateNew.getImages().clear();
		final List<RawResponse> rawResponses = new ArrayList<>(estateNew.getRawResponses());
		estateNew.getRawResponses().clear();

		List<Estate> duplicateEstates = findDuplicateEstates(estateNew);
		if (duplicateEstates.isEmpty()) {
			// novy
			this.estateRepository.save(estateNew);
		} else {
			// duplicitni
			Estate estateOld = duplicateEstates.get(0);
			Long duplicityId = estateOld.getDuplicityId();
			if (duplicityId == 0) {
				duplicityId = estateOld.getId();
			}

			estateNew.setDuplicityId(duplicityId);
			estateNew.setVisible(estateOld.getVisible());
			estateNew.setStars(estateOld.getStars());
			estateNew.setDateSort(estateOld.getDateSort());
			resetDateSortIfPriceChanged(estateNew, estateOld.getPrice(), estateNew.getPrice());
			this.estateRepository.save(estateNew);
			this.historyRepository.save(new History(estateNew, HistoryType.DUPLICITY, "Duplicitni stary: " + estateOld.getUrl()));
			if (estateOld.getPrice() != estateNew.getPrice()) {
				this.historyRepository.save(new History(estateNew, HistoryType.PRICE, "Cena: " + estateOld.getPrice() + " -> " + estateNew.getPrice()));
			}

			estateOld.setDuplicityId(duplicityId);
			this.estateRepository.save(estateOld);
			this.historyRepository.save(new History(estateOld, HistoryType.DUPLICITY, "Duplicitni novy: " + estateNew.getUrl()));
			
			if (!duplicateEstates.isEmpty()) {
				for (final Estate estate : duplicateEstates) {
					if (!estate.getDuplicityId().equals(duplicityId)) {
						log.error("Expected duplicityId=" + duplicityId + " in Estate(id=" + estate.getId()
								+ ", duplicityId=" + estate.getDuplicityId() + "url=" + estate.getUrl() + ")");
					}
				}
			}
		}

		this.imageRepository.saveAll(images);
		this.rawResponseRepository.saveAll(rawResponses);
	}

	@Transactional
	public void convertAndUpdate(Estate estateOld, Estate estateNew) {
		estateOld = this.estateRepository.getOne(estateOld.getId());

		resetDateSortIfPriceChanged(estateOld, estateOld.getPrice(), estateNew.getPrice());

		estateOld.setActive(true);
		estateOld.setAreaBuild(estateNew.getAreaBuild());
		estateOld.setAreaFloor(estateNew.getAreaFloor());
		estateOld.setAreaGarden(estateNew.getAreaGarden());
		estateOld.setAreaTotal(estateNew.getAreaTotal());
		estateOld.setAreaUsable(estateNew.getAreaUsable());
		estateOld.setAddress(estateNew.getAddress());
		estateOld.setDescription(estateNew.getDescription());
		estateOld.setLatitude(estateNew.getLatitude());
		estateOld.setLongitude(estateNew.getLongitude());
		estateOld.setMetaDescription(estateNew.getMetaDescription());
		estateOld.setPrice(estateNew.getPrice());
		estateOld.setSrealityId(estateNew.getSrealityId());
		estateOld.setState(estateNew.getState());
		estateOld.setTitle(estateNew.getTitle());
		estateOld.setUrl(estateNew.getUrl());
		estateOld.setZoom(estateNew.getZoom());

		// TODO: what will happen on update with old/new images and response?
		this.estateRepository.save(estateOld);
		if (!estateOld.getActive()) {
			this.historyRepository.save(new History(estateOld, HistoryType.ACTIVE, "Vlozeno"));
		}
	}

	private void resetDateSortIfPriceChanged(Estate estate, Long priceOld, Long priceNew) {
		if (priceOld != null || priceNew != null) {
			if (priceOld == null || priceNew == null || priceOld - priceNew > priceDiffThreshold) {
				estate.setDateSort(new Date());
			}
		}
	}

	@Transactional
	public int updateInactive(final Set<Long> srealityIds) {
		int countDeleted = 0;
		List<Estate> estates = this.findAllActive();
		for (Estate estate : estates) {
			if (!srealityIds.contains(estate.getSrealityId())) {
				estate.setActive(false);
				this.estateRepository.save(estate);
				this.historyRepository.save(new History(estate, HistoryType.ACTIVE, "Smazano"));
				countDeleted++;
			}
		}
		return countDeleted;
	}

	@Transactional
	public void setStarsFor(final long srealityId, final int stars) {
		this.estateRepository.setStarsFor(srealityId, stars);
	}

	@Transactional
	public void setVisibleFor(final long srealityId, final boolean visible) {
		this.estateRepository.setVisibleFor(srealityId, visible);
	}

	@Transactional
	public void setNoteFor(final long srealityId, final String note) {
		this.estateRepository.setNoteFor(srealityId, note);
	}
}
