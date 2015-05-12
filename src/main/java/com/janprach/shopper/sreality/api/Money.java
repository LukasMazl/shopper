package com.janprach.shopper.sreality.api;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class Money {
	@JsonProperty("name")
	private String name;
	@JsonProperty("unit")
	private String unit;
	@JsonProperty("value")
	private String value;
	@JsonProperty("value_raw")
	private Long valueRaw;
}
