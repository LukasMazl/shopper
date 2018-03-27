package com.janprach.shopper.sreality.config;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
@Component
public class JacksonCacheKeyGenerator implements KeyGenerator {
	private final ObjectMapper objectMapper;

	@Override
	public Object generate(final Object target, final Method method, final Object... params) {
		try {
			return objectMapper.writeValueAsString(new Key(target.getClass().getName(), method.getName(), params));
		} catch (final JsonProcessingException e) {
			log.error("Error serializing key ", e);
			return null;
		}
	}

	@Value
	public static class Key {
		private final String className;
		private final String methodName;
		private final Object[] params;
	}
}
