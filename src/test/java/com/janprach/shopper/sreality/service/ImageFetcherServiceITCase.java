package com.janprach.shopper.sreality.service;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Test;
import org.springframework.cache.annotation.EnableCaching;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.janprach.shopper.ShopperITBase;

import lombok.val;

@EnableCaching
//@org.junit.Ignore("This query the actual sreality server.")
public class ImageFetcherServiceITCase extends ShopperITBase {
	@Inject
	EstateFetcherService estateFetcherService;
	@Inject
	ImageFetcherService imageFetcherService;
	@Inject
	ObjectMapper objectMapper;

	@Test
	public void fetchSrealityImage() throws Exception {
		val estateListing = estateFetcherService.fetchEstateListing(1, 10);
		assertThat(estateListing).isPresent();
		val firstEstate = estateListing.map(el -> el.getEmbedded().getEstates().get(0));
		System.out.println(">>>> firstEstate: " + firstEstate.map(e -> e.getHashId()));
		val estate = firstEstate.flatMap(e -> estateFetcherService.fetchEstate(e.getHashId()));
		assertThat(estate).isPresent();
		val imageUri = estate.get().getEmbedded().getImages().get(0).getLinks().getView().getHref();
		System.out.println(">>>> imageUri: " + imageUri);
		val imagesMetaDataOption = imageFetcherService.fetchAndSaveImage(imageUri);
		assertThat(imagesMetaDataOption).isPresent();
		val imagesMetaData = imagesMetaDataOption.get();
		System.out.println(">>>> imagesMetaData: " + imagesMetaData.getSha1());
		System.out.println(objectMapper.writeValueAsString(imagesMetaData));
		assertThat(imagesMetaData.getWidth()).isGreaterThan(100);
		assertThat(imagesMetaData.getHeight()).isGreaterThan(100);
		assertThat(imagesMetaData.getSha1()).hasSize(40);
	}
}
