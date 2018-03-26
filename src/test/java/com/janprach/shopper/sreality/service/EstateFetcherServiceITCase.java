package com.janprach.shopper.sreality.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;

import javax.inject.Inject;

import org.assertj.core.api.Condition;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.janprach.shopper.ShopperITBase;

import lombok.val;

//@org.junit.Ignore("This query the actual sreality server.")
public class EstateFetcherServiceITCase extends ShopperITBase {
	@Inject
	EstateFetcherService estateFetcherService;
	@Inject
	ObjectMapper objectMapper;

	@Test
	public void testFetchLatestEstateSummariesAndLatestEstate() throws Exception {
		val estateSummaries = estateFetcherService.fetchEstateSummaries().limit(42).collect(Collectors.toList());
//		estateSummaries.forEach(es -> System.out.println(estateFetcherService.fetchEstate(es.getHashId()).get().getName()));
		assertThat(estateSummaries.size()).isEqualTo(42);
		val estateNames = estateSummaries.stream().map(es -> es.getName()).collect(Collectors.toList());
//		estateNames.forEach(en -> System.out.println(en));
		assertThat(estateNames).haveAtLeastOne(new Condition<String>(en -> en.contains("Prodej"), "contains 'Prodej'"));

		val estateSummary = estateSummaries.get(0);
		System.out.println(">>>> estateSummary: " + estateSummary.getName());
		System.out.println(objectMapper.writeValueAsString(estateSummary));

		val estate = estateFetcherService.fetchEstate(estateSummary.getHashId());
		assertThat(estate).isPresent();
		if (estate.isPresent()) {
			System.out.println(">>>> estate: " + estate.get().getName() + " at " + estate.get().getMap());
			System.out.println(objectMapper.writeValueAsString(estate.get()));
		}
	}
}
