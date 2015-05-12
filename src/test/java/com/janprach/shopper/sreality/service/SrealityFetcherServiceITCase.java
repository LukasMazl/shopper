package com.janprach.shopper.sreality.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.val;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.janprach.shopper.ShopperApp;

@Ignore("This query the actual sreality server.")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShopperApp.class)
public class SrealityFetcherServiceITCase {
	@Inject
	SrealityFetcherService srealityFetcher;

	@Test
	public void testFetchLatestRealites() {
		val estateSummaries = this.srealityFetcher.fetchEstateSummaries();
		val estateNames = estateSummaries.limit(100).map(es -> es.getName()).collect(Collectors.toList());
		assertThat(estateNames.size()).isEqualTo(100);
		estateNames.forEach(en -> System.out.println(en));
	}
}
