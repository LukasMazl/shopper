package com.janprach.shopper.sreality.service;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.janprach.shopper.sreality.entity.Estate;
import com.janprach.shopper.sreality.repository.EstateRepository;
import com.janprach.shopper.sreality.repository.ImageRepository;
import com.janprach.shopper.sreality.repository.RawResponseRepository;

@Slf4j
@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
@Component("estateService")
@Transactional
public class EstateService {
	private final EstateRepository estateRepository;
	private final ImageRepository imageRepository;
	private final RawResponseRepository rawResponseRepository;

	@Transactional(readOnly = true)
	public Estate findBySrealityId(final long srealityId) {
		return this.estateRepository.findBySrealityId(srealityId);
	}

	@Transactional(readOnly = true)
	public List<Estate> findAllActive() {
		return this.estateRepository.findAllByActive(true);
	}

	@Transactional
	void saveEstate(final Estate estate) {
		long id = estate.getId();
		if (id == 0)
			log.info("Insert" + estate.getAddress() + ", " + estate.getUrl());
		else
			log.info("Update" + estate.getAddress() + ", " + estate.getUrl());
		try {
			this.estateRepository.save(estate);
			if (id == 0) {
				this.imageRepository.save(estate.getImages());
				this.rawResponseRepository.save(estate.getRawResponses());
			}
		} catch (final Exception e) {
			log.error("Failed saving estate id {}.", estate.getSrealityId(), e);
		}
	}
	
	public static void addHistory(final Estate estate, final String message) {
		String log = DateFormatUtils.format(new Date(), "dd.MM.yyyy") + " " + message;
		
		if (StringUtils.isEmpty(estate.getHistory()))
			estate.setHistory(log);
		else
			estate.setHistory(log + "\n" + estate.getHistory());
	}

	@Transactional
	public void setStarsFor(final long srealityId, final int stars) {
		this.estateRepository.setStarsFor(srealityId, stars);
	}
}
