package com.janprach.shopper.sreality.api;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class Link {
	@JsonProperty("href")
	private String href;
	@JsonProperty("profile")
	private String profile;
	@JsonProperty("title")
	private String title;
}
