package com.janprach.shopper.sreality.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;

import javax.inject.Inject;

import org.assertj.core.api.Condition;
import org.junit.Test;

import com.janprach.shopper.ShopperITBase;

import lombok.val;

//@org.junit.Ignore("This query the actual sreality server.")
public class SrealityFetcherServiceITCase extends ShopperITBase {
	@Inject
	SrealityFetcherService srealityFetcher;

	@Test
	public void testFetchLatestRealites() {
		val estateSummaries = this.srealityFetcher.fetchEstateSummaries();
		val estateNames = estateSummaries.limit(42).map(es -> es.getName()).collect(Collectors.toList());
		assertThat(estateNames.size()).isEqualTo(42);
//		estateNames.forEach(en -> System.out.println(en));
		assertThat(estateNames).haveAtLeastOne(new Condition<String>(en -> en.contains("Prodej"), "contains 'Prodej'"));
	}
}
