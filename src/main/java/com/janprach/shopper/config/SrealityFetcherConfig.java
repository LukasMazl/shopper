package com.janprach.shopper.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "com.janprach.shopper.sreality")
public class SrealityFetcherConfig {
	private String categoryMain; // 2 = domy
	private String categorySub; // 37|39 = rodinne-domy nebo vily
	private String categoryType; // 1 = prodej
	private String localityRegion; // 10 = praha
	private String localityDistrict; // 5006 = praha-6
	private String priceRange; // 0|15000000 = od 0 do 15000000

	private String userAgentString;
}
