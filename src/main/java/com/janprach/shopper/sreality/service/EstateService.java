package com.janprach.shopper.sreality.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

	@Transactional
	void saveEstate(final Estate estate) {
		log.info("Saving estate id {} ...", estate.getSrealityId());
		try {
			this.estateRepository.save(estate);
			this.imageRepository.save(estate.getImages());
			this.rawResponseRepository.save(estate.getRawResponses());
		} catch (final Exception e) {
			log.error("Failed saving estate id {}.", estate.getSrealityId(), e);
		}
	}
	
	@Transactional
	public void setStarsFor(final long srealityId, final int stars) {
		this.estateRepository.setStarsFor(srealityId, stars);
	}
}
