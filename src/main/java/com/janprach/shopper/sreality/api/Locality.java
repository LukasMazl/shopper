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
	@JsonProperty("locality_region_id")
	private String localityRegionId;
}
