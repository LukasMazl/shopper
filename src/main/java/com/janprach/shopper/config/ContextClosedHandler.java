package com.janprach.shopper.config;

import javax.ws.rs.client.Client;

import lombok.AllArgsConstructor;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
public class ContextClosedHandler implements ApplicationListener<ContextClosedEvent> {
	private final Client jerseyClient;

	public void onApplicationEvent(final ContextClosedEvent event) {
		this.jerseyClient.close();
	}
}
