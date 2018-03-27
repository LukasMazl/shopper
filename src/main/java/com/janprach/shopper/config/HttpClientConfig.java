package com.janprach.shopper.config;

import javax.inject.Inject;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.val;

@Configuration
public class HttpClientConfig {
	@Inject
	private SrealityFetcherConfig srealityFetcherConfig;

	@Bean
	public CloseableHttpClient closeableHttpClient() {
		val clientBuilder = HttpClientBuilder.create();
		clientBuilder.setUserAgent(this.srealityFetcherConfig.getUserAgentString());
		return clientBuilder.build();
	}
}
