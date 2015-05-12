package com.janprach.shopper.sreality.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

//@JsonIgnoreProperties({ "images", "image_middle2", "broader_search", "local_search", "dynamic", "rss" })
@Data
public class Links {
	@JsonProperty("self")
	private Link self;
}
