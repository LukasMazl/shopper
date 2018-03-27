package com.janprach.shopper.sreality.service;

import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
@Component
public class RichHttpClient {
	private final ObjectMapper objectMapper;
	private final RichHttpClientHelper richHttpClientHelper;

	// The helper class is just a hack allowing us to call getByteArray
	// with caching aspect woven into richHttpClientHelper.
	@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
	@Component
	public static class RichHttpClientHelper {
		private final CloseableHttpClient closeableHttpClient;

//		@Cacheable(cacheNames = "httpClient", key = "{ #httpRequest?.method, #httpRequest?.getURI() }")
		@Cacheable(cacheNames = "httpClient", keyGenerator = "jacksonCacheKeyGenerator")
		public Optional<byte[]> getByteArray(final HttpUriRequest httpRequest) {
			try {
				val response = closeableHttpClient.execute(httpRequest);
				try {
					val httpEntity = Optional.ofNullable(response.getEntity());
					if (httpEntity.isPresent()) {
						return Optional.ofNullable(IOUtils.toByteArray(httpEntity.get().getContent()));
					}
				} finally {
					response.close();
				}
			} catch (final Exception e) {
				log.error("Error fetching " + httpRequest, e);
			}
			return Optional.empty();
		}
	}

	public Optional<byte[]> getByteArray(final HttpUriRequest httpRequest) {
		return this.richHttpClientHelper.getByteArray(httpRequest);
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
