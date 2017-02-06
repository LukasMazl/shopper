package com.janprach.shopper.sreality.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import com.janprach.shopper.ShopperITBase;
import com.janprach.shopper.sreality.entity.Estate;

public class EstateRepositoryITCase extends ShopperITBase {
	@Inject
	EstateRepository repository;

	@Test
	public void findFirstPageOfCities() {
		List<Estate> estates = this.repository.findAll();
		assertThat(estates.size()).isGreaterThan(0);
	}
}
