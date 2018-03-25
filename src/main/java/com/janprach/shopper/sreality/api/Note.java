package com.janprach.shopper.sreality.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Note {
	@JsonProperty("_links")
	private Links links;
	@JsonProperty("has_note")
	private Boolean hasNote;
	@JsonProperty("note")
	private String note;
}
