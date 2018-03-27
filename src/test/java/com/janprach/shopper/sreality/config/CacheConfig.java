package com.janprach.shopper.sreality.config;

import java.io.File;
import java.time.Duration;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.cache.Caching;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;
import org.ehcache.impl.config.persistence.DefaultPersistenceConfiguration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Configuration;

import lombok.val;

@Configuration
public class CacheConfig extends CachingConfigurerSupport {
	@Override
	public CacheManager cacheManager() {
		val cacheEventListenerConfiguration = CacheEventListenerConfigurationBuilder
			    .newEventListenerConfiguration(new LoggingCacheEventListener(), EnumSet.allOf(EventType.class)) 
			    .unordered().asynchronous();
		val persistenceConfiguration = new DefaultPersistenceConfiguration(new File("target", "ehcache.data"));
		val resourcePools = ResourcePoolsBuilder.newResourcePoolsBuilder().disk(256, MemoryUnit.MB, true).build();
		val cacheConfiguration = CacheConfigurationBuilder
				.newCacheConfigurationBuilder(Object.class, Object.class, resourcePools)
				.withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(300)))
				.add(cacheEventListenerConfiguration)
				.build();
		Map<String, org.ehcache.config.CacheConfiguration<?, ?>> caches = new HashMap<>();
		caches.put("httpClient", cacheConfiguration);
		val provider = (EhcacheCachingProvider) Caching.getCachingProvider();
		val configuration = new DefaultConfiguration(caches, provider.getDefaultClassLoader(),
				persistenceConfiguration);
		val ehCacheManager = provider.getCacheManager(provider.getDefaultURI(), configuration);
		return new JCacheCacheManager(ehCacheManager);
	}

	public static class LoggingCacheEventListener implements CacheEventListener<Object, Object> {
		private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoggingCacheEventListener.class);

		@Override
		public void onEvent(CacheEvent<? extends Object, ? extends Object> event) {
			log.info("> {} key = {}", event.getType(), event.getKey());
		}
	}
}
