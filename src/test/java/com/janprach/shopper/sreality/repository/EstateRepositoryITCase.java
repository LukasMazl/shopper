package com.janprach.shopper.sreality.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.janprach.shopper.ShopperApp;
import com.janprach.shopper.sreality.entity.Estate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShopperApp.class)
public class EstateRepositoryITCase {
	@Inject
	EstateRepository repository;

	@Test
	public void findFirstPageOfCities() {
		List<Estate> estates = this.repository.findAll();
		assertThat(estates.size()).isGreaterThan(0);
	}
}
