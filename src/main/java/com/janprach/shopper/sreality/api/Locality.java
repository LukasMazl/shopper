package com.janprach.shopper.sreality.api;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class Locality {
	@JsonProperty("category_main_cb")
	private String categoryMainCb;
	@JsonProperty("category_sub_cb")
	private String categorySubCb;
	@JsonProperty("category_type_cb")
	private String categoryTypeCb;
	@JsonProperty("locality")
	private String locality;

	// Filter only fields (should we make separate class Filter?)
	@JsonProperty("locality_region_id")
	private String localityRegionId;
	@JsonProperty("suggested_districtId")
	private Long suggestedDistrictId;
	@JsonProperty("suggested_regionId")
	private Long suggestedRegionId;
}
