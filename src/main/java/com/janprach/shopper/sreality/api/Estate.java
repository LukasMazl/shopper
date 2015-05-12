package com.janprach.shopper.sreality.api;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Estate {
	@JsonProperty("_embedded")
	private EstateEmbedded embedded;
	@JsonProperty("_links")
	private Links links;
	@JsonProperty("is_topped")
	private Boolean isTopped;
	@JsonProperty("is_topped_today")
	private Boolean isToppedToday;
	@JsonProperty("items")
	private List<Item> items = new ArrayList<Item>();;
	@JsonProperty("locality")
	private NamedValue locality;
	@JsonProperty("logged_in")
	private Boolean loggedIn;
	@JsonProperty("map")
	private Geo map;
	@JsonProperty("meta_description")
	private String metaDescription;
	@JsonProperty("name")
	private NamedValue name;
	@JsonProperty("price_czk")
	private Money priceCzk;
	@JsonProperty("rus")
	private Boolean rus;
	@JsonProperty("seo")
	private Locality seo;
	@JsonProperty("text")
	private NamedValue text;

	// @JsonIgnore
	// private java.util.Map<String, Object> additionalProperties = new
	// HashMap<String, Object>();

	@Data
	public static class EstateEmbedded {
		@JsonProperty("calculator")
		private Object calculator;
		@JsonProperty("favourite")
		private Object favourite;
		@JsonProperty("images")
		private List<Image> images = new ArrayList<Image>();
		@JsonProperty("note")
		private Note note;
		@JsonProperty("seller")
		private Object seller;

		@Data
		public static class Image {
			@JsonProperty("_links")
			private Links links;
			@JsonProperty("id")
			private Long id;
			@JsonProperty("order")
			private Long order;
		}

		@Data
		public static class Note {
			@JsonProperty("_links")
			private Links links;
			@JsonProperty("has_note")
			private Boolean hasNote;
			@JsonProperty("note")
			private String note;
		}
	}

	@Data
	public static class Item {
		@JsonProperty("currency")
		private String currency;
		@JsonProperty("name")
		private String name;
		@JsonProperty("negotiation")
		private Boolean negotiation;
		@JsonProperty("notes")
		private List<Object> notes = new ArrayList<Object>();
		@JsonProperty("type")
		private String type;
		@JsonProperty("unit")
		private String unit;
		@JsonProperty("value")
		private Object value;
		@JsonProperty("topped")
		private Boolean topped;
	}

	@Data
	public static class Geo {
		@JsonProperty("bounding_box")
		private BoundingBox boundingBox;
		@JsonProperty("geometry")
//		private List<Geometry> geometry = new ArrayList<Geometry>();
//		private Geometry geometry;
		private Object geometry;
		@JsonProperty("ppx")
		private Long ppx;
		@JsonProperty("ppy")
		private Long ppy;
		@JsonProperty("type")
		private String type;
		@JsonProperty("zoom")
		private Integer zoom;

		@Data
		public static class Geometry {
			@JsonProperty("data")
			private List<List<Double>> data = new ArrayList<List<Double>>();
			@JsonProperty("type")
			private String type;
		}

		@Data
		public static class BoundingBox {
			@JsonProperty("leftBottomBounding")
			private Point2D leftBottomBounding;
			@JsonProperty("rightTopBounding")
			private Point2D rightTopBounding;

			@Data
			public static class Point2D {
				@JsonProperty("x")
				private Long x;
				@JsonProperty("y")
				private Long y;
			}
		}
	}

	@Data
	public static class NamedValue {
		@JsonProperty("name")
		private String name;
		@JsonProperty("value")
		private String value;
	}
}
