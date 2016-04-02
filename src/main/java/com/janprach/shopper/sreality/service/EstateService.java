package com.janprach.shopper.sreality.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
@Component("estateService")
@Transactional
public class EstateService {
	private final EstateRepository estateRepository;
	private final HistoryRepository historyRepository;
	private final ImageRepository imageRepository;
	private final RawResponseRepository rawResponseRepository;
	@Value("${com.janprach.shopper.priceDiffThreshold}")
	private final int priceDiffThreshold;
	
	@Transactional(readOnly = true)
	public Estate findBySrealityId(final long srealityId) {
		return this.estateRepository.findBySrealityId(srealityId);
	}

	@Transactional(readOnly = true)
	public List<Estate> findAllActive() {
		return this.estateRepository.findAllByActive(true);
	}

	@Transactional
	public boolean convertAndInsert(Estate estateNew) {
		saveEstate(estateNew);
		return true;
	}

	@Transactional
	public boolean convertAndUpdate(Estate estateOld, Estate estateNew) {
		// TODO: what will happen on update with old/new images and response?
		estateOld = this.estateRepository.findOne(estateOld.getId());

		resetDateSortIfPriceChanged(estateOld, estateOld.getPrice(), estateNew.getPrice());
		if (!estateOld.getActive())
			EstateUtils.addHistoryRecord(estateOld, HistoryType.ACTIVE, "Vlozeno");

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
	public int updateInactive(final HashSet<Long> srealityIds) {
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
	public void setNoteFor(final long srealityId, final String note) {
		this.estateRepository.setNoteFor(srealityId, note);
	}
}
