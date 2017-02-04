package com.janprach.shopper.sreality.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

//import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.janprach.shopper.sreality.entity.Estate;
import com.janprach.shopper.sreality.entity.HistoryType;
import com.janprach.shopper.sreality.repository.EstateRepository;
import com.janprach.shopper.sreality.repository.HistoryRepository;
import com.janprach.shopper.sreality.repository.ImageRepository;
import com.janprach.shopper.sreality.repository.RawResponseRepository;
import com.janprach.shopper.sreality.util.EstateUtils;

@Slf4j
//@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
@Component("estateService")
@Transactional
public class EstateService {
	@javax.inject.Inject
	public EstateService(EstateRepository estateRepository, HistoryRepository historyRepository,
			ImageRepository imageRepository, RawResponseRepository rawResponseRepository, @Value("${com.janprach.shopper.sreality.priceDiffThreshold}") int priceDiffThreshold) {
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
 	public List<Estate> findAllDuplicate(final Estate estate) {
 		if (estate.getAddress().contains(",")) {
 			List<Estate> estatesDuplicate1 = this.estateRepository.findAllDuplicateByAddressByPrice(
 					StringUtils.substringBefore(estate.getAddress(), ",") + "%",
 					estate.getPrice(), estate.getAreaTotal(), estate.getAreaUsable());
 			if (!estatesDuplicate1.isEmpty())
 				return estatesDuplicate1;
 		}

 		List<Estate> estatesDuplicate2 = this.estateRepository.findAllDuplicateByPrice(
 				estate.getPrice(), estate.getAreaTotal(), estate.getAreaUsable());
 		if (!estatesDuplicate2.isEmpty())
 			return estatesDuplicate2;

 		if (estate.getAddress().contains(",")) {
 			List<Estate> estatesDuplicate3 = this.estateRepository.findAllDuplicateByAddress(
 					estate.getAddress(),estate.getAreaTotal(), estate.getAreaUsable());
 			if (!estatesDuplicate3.isEmpty())
 				return estatesDuplicate3;
 		}
 		
 		return new ArrayList<Estate>();
 	}

	@Transactional
	public boolean convertAndInsert(Estate estateNew) {
		List<Estate> duplicateEstates = findAllDuplicate(estateNew);
		if (duplicateEstates.isEmpty()) {
			// novy
			saveEstate(estateNew);
		} else {
			// duplicitni
			Estate estateOld = duplicateEstates.get(0);
			Long duplicityId = estateOld.getDuplicityId();
			if (duplicityId == 0)
				duplicityId = estateOld.getId();

			estateNew.setDuplicityId(duplicityId);
			estateNew.setVisible(estateOld.getVisible());
			estateNew.setStars(estateOld.getStars());
			estateNew.setDateSort(estateOld.getDateSort());
			resetDateSortIfPriceChanged(estateNew, estateOld.getPrice(), estateNew.getPrice());
			EstateUtils.addHistoryRecord(estateNew, HistoryType.DUPLICITY, "Duplicitni stary: " + estateOld.getUrl());
			saveEstate(estateNew);
			
			estateOld.setDuplicityId(duplicityId);
			EstateUtils.addHistoryRecord(estateOld, HistoryType.DUPLICITY, "Duplicitni novy: " + estateNew.getUrl());
			saveEstate(estateOld);
			
			if (duplicateEstates.size() > 1)
			{
				for (Estate estate : duplicateEstates) {
					if (!estate.getDuplicityId().equals(duplicityId))
						log.error("Another duplicityId: " + estate.getDuplicityId() + " - " + estate.getUrl());
				}
			}
		}
		
		return true;
	}

	@Transactional
	public boolean convertAndUpdate(Estate estateOld, Estate estateNew) {
		// TODO: what will happen on update with old/new images and response?
		estateOld = this.estateRepository.findOne(estateOld.getId());

		if (!estateOld.getActive())
			EstateUtils.addHistoryRecord(estateOld, HistoryType.ACTIVE, "Vlozeno");
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
		saveEstate(estateOld);
		return true;
	}

	private void resetDateSortIfPriceChanged(Estate estate, Long priceOld, Long priceNew) {
		// jen pri vetsi sleve chceme radit nahoru
		if (priceOld - priceNew > priceDiffThreshold)
			estate.setDateSort(new Date());
		if (!priceNew.equals(priceOld))
			EstateUtils.addHistoryRecord(estate, HistoryType.PRICE, "Cena: " + priceOld + " -> " + priceNew);
	}

	@Transactional
	public int updateInactive(final Set<Long> srealityIds) {
		int countDeleted = 0;
		List<Estate> estates = this.findAllActive();
		for (Estate estate : estates) {
			if (!srealityIds.contains(estate.getSrealityId())) {
				estate.setActive(false);
				EstateUtils.addHistoryRecord(estate, HistoryType.ACTIVE, "Smazano");
				saveEstate(estate);
				countDeleted++;
			}
		}
		return countDeleted;
	}

	@Transactional
	void saveEstate(final Estate estate) {
		final boolean isNew = estate.getId() == 0;
		if (isNew) {
			log.info("Insert " + estate.getAddress() + ", " + estate.getUrl());
		} else {
			log.info("Update " + DateFormatUtils.format(estate.getDateSort(), "dd.MM.yyyy")
					+ ", " + estate.getAddress() + ", " + estate.getUrl());
		}
		try {
			this.estateRepository.save(estate);
			this.historyRepository.save(estate.getHistories());
			if (isNew) {
				this.imageRepository.save(estate.getImages());
				this.rawResponseRepository.save(estate.getRawResponses());
			}
		} catch (final Exception e) {
			log.error("Failed saving estate id {}.", estate.getSrealityId(), e);
		}
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
