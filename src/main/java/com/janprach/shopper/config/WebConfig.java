package com.janprach.shopper.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	private static final int YEAR_IN_SECONDS = 31558150;

	@Inject
	private SrealityFetcherConfig srealityFetcherConfig;

	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/original/**")
				.addResourceLocations("file:" + this.srealityFetcherConfig.getImagesDirectory() + "/")
				.setCachePeriod(YEAR_IN_SECONDS);
		registry.addResourceHandler("/images/thumbnails/**")
				.addResourceLocations("file:" + this.srealityFetcherConfig.getThumbnailsDirectory() + "/")
				.setCachePeriod(YEAR_IN_SECONDS);
	}
}
