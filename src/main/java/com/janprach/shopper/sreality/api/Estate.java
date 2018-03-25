package com.janprach.shopper.sreality.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Estate {
	@JsonProperty("_embedded")
	private EstateEmbedded embedded;
	@JsonProperty("_links")
	private Links links;
	@JsonProperty("codeItems")
	private CodeItems codeItems;
	@JsonProperty("is_topped")
	private Boolean isTopped;
	@JsonProperty("is_topped_today")
	private Boolean isToppedToday;
	@JsonProperty("items")
	private List<Item> items = new ArrayList<>();
	@JsonProperty("locality")
	private NamedValue locality;
	@JsonProperty("locality_district_id")
	private Long localityDistrictId;
	@JsonProperty("logged_in")
	private Boolean loggedIn;
	@JsonProperty("map")
	private Geo map;
	@JsonProperty("meta_description")
	private String metaDescription;
	@JsonProperty("name")
	private NamedValue name;
	@JsonProperty("panorama")
	private Boolean panorama;
	@JsonProperty("poi")
	private List<Pous> poi = null;
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
	public static class CodeItems {
		@JsonProperty("building_type_search")
		private Long buildingTypeSearch;
		@JsonProperty("estate_area")
		private Long estateArea;
		@JsonProperty("object_type")
		private Long objectType;
		@JsonProperty("something_more1")
		private Object somethingMore1;
		@JsonProperty("something_more2")
		private Object somethingMore2;
	}

	@Data
	public static class EstateEmbedded {
		@JsonProperty("calculator")
		private Object calculator;
		@JsonProperty("favourite")
		private Object favourite;
		@JsonProperty("images")
		private List<Image> images = new ArrayList<>();
		@JsonProperty("note")
		private Note note;
		@JsonProperty("seller")
		private Object seller;

		@Data
		public static class Image {
			@JsonProperty("_links")
			private ImageLinks links;
			@JsonProperty("id")
			private Long id;
			@JsonProperty("kind")
			private Long kind;
			@JsonProperty("order")
			private Long order;

			@Data
			public static class ImageLinks {
				@JsonProperty("self")
				private Link self;
				@JsonProperty("gallery")
				private Link gallery;
				@JsonProperty("view")
				private Link view;
				@JsonIgnore
				private Map<String, Object> additionalProperties = new HashMap<>();
			}
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
		private List<Note> notes = new ArrayList<>();
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
//		private List<Geometry> geometry = new ArrayList<>();
//		private Geometry geometry;
		private Object geometry;
		@JsonProperty("lat")
		private Double lat;
		@JsonProperty("lon")
		private Double lon;
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
			private List<List<Double>> data = new ArrayList<>();
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

	@Data
	public static class Pous {
		@JsonProperty("description")
		private String description;
		@JsonProperty("distance")
		private Long distance;
		@JsonProperty("imgUrl")
		private String imgUrl;
		@JsonProperty("lat")
		private Double lat;
		@JsonProperty("lon")
		private Double lon;
		@JsonProperty("name")
		private String name;
		@JsonProperty("time")
		private Long time;
		@JsonProperty("url")
		private String url;
		@JsonProperty("walkDistance")
		private Long walkDistance;
	}
}
