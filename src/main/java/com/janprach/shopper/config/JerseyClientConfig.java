package com.janprach.shopper.config;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import lombok.val;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyClientConfig {
	@Bean
	public Client client() {
		val clientBuilder = ClientBuilder.newBuilder();
		clientBuilder.property(ClientProperties.ASYNC_THREADPOOL_SIZE, 12);
		clientBuilder.property(ClientProperties.CONNECT_TIMEOUT, 10000);
		clientBuilder.property(ClientProperties.FOLLOW_REDIRECTS, true);
		clientBuilder.property(ClientProperties.READ_TIMEOUT, 30000);
		clientBuilder.register(ObjectMapperProvider.class);
		clientBuilder.register(JacksonFeature.class);
		return clientBuilder.build();
	}
}
