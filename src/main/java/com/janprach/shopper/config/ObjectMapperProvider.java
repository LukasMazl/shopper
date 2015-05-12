package com.janprach.shopper.config;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import lombok.val;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
	@Override
	public ObjectMapper getContext(final Class<?> type) {
		val objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		// objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		return objectMapper;
	}
}
