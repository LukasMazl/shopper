package com.janprach.shopper.sreality.api;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

// From ImageLinks: "gallery", "view"
//@JsonIgnoreProperties({ "images", "image_middle2", "broader_search", "local_search", "dynamic", "dynamicUp", "dynamicDown", "rss", "iterator", "clusters_with_bounding_box_of_first_10", "broader_search", "local_search" })
@Data
public class Links {
    @JsonProperty("self")
    private Link self;

    @JsonIgnore
//	@JsonAnySetter
	private Map<String, Object> additionalProperties = new HashMap<>();
}
