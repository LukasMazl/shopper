package com.janprach.shopper.config;

import lombok.val;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

@Configuration
public class RepositoryRestMvcConfig extends RepositoryRestMvcConfiguration {
	@Override
	public RepositoryRestConfiguration config() {
		val config = super.config();
		config.setBasePath("/api/v1");
		return config;
	}
}
