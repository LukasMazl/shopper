package com.janprach.shopper.sreality.service;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
@Component
public class RichHttpClient {
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;

	public Optional<byte[]> getByteArray(final HttpUriRequest httpRequest) {
		return Optional.of(httpRequest).map(request -> {
			try {
				return httpClient.execute(request);
			} catch (final IOException e) {
				log.error("Error fetching " + httpRequest, e);
				return null;
			}
		}).map(response -> response.getEntity()).map(httpEntity -> {
			try {
				return IOUtils.toByteArray(httpEntity.getContent());
			} catch (final UnsupportedOperationException | IOException e) {
				log.error("Error fetching " + httpRequest, e);
				return null;
			}
		});
	}

	public <T> Optional<T> getEntity(final HttpUriRequest httpRequest, final Class<T> valueType) {
		return getEntityWithRawResponse(httpRequest, valueType).map(ewrr -> ewrr.getValue());
	}

	public <T> Optional<EntityWithRawResponse<T>> getEntityWithRawResponse(final HttpUriRequest httpRequest,
			final Class<T> valueType) {
		return getByteArray(httpRequest).map(ba -> {
			try {
				return new EntityWithRawResponse<T>(objectMapper.readValue(ba, valueType), ba);
			} catch (final Exception e) {
				log.error("Error parsing " + httpRequest, e);
				return null;
			}
		});
	}

	public static RequestBuilder addNonNullParameter(final RequestBuilder requestBuilder, final String name,
			final String value) {
		if (value == null) {
			return requestBuilder;
		} else {
			return requestBuilder.addParameter(name, value);
		}
	}

	@Value
	public static class EntityWithRawResponse<T> {
		private final T value;
		private final byte[] rawResponse;
	}
}
