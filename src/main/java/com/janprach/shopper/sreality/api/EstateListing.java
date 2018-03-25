package com.janprach.shopper.sreality.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class EstateListing {
	@JsonProperty("_embedded")
	private EstateListingEmbedded embedded;
	@JsonProperty("_links")
	private Links links;
	@JsonProperty("category_instrumental")
	private String categoryInstrumental;
	@JsonProperty("filter")
	private Locality filter;
	@JsonProperty("filterLabels")
	private List<String> filterLabels = new ArrayList<>();
	@JsonProperty("filterLabels2")
	private Map<String, String> filterLabels2 = new HashMap<>();
	@JsonProperty("locality")
	private String locality;
	@JsonProperty("locality_dativ")
	private String localityDativ;
	@JsonProperty("logged_in")
	private Boolean loggedIn;
	@JsonProperty("meta_description")
	private String metaDescription;
	@JsonProperty("page")
	private Long page;
	@JsonProperty("per_page")
	private Long perPage;
	@JsonProperty("result_size")
	private Long resultSize;
	@JsonProperty("title")
	private String title;

	// @JsonIgnore
	// private Map<String, Object> additionalProperties = new HashMap<String,
	// Object>();

	@Data
	public static class EstateListingEmbedded {
		@JsonProperty("estates")
		private List<EstateSummary> estates = new ArrayList<EstateSummary>();
		@JsonProperty("is_saved")
		private Object isSaved;
		@JsonProperty("not_precise_location_count")
		private Object notPreciseLocationCount;

		@Data
		public static class EstateSummary {
			@JsonProperty("_embedded")
			private EstateSummaryEmbedded embedded;
			@JsonProperty("_links")
			private Links links;
			@JsonProperty("attractive_offer")
			private Long attractiveOffer;
			@JsonProperty("auctionPrice")
			private Long auctionPrice;
			@JsonProperty("category")
			private Long category;
			@JsonProperty("gps")
			private Gps gps;
			@JsonProperty("has_floor_plan")
			private Boolean hasFloorPlan;
			@JsonProperty("has_panorama")
			private Boolean hasPanorama;
			@JsonProperty("has_video")
			private Boolean hasVideo;
			@JsonProperty("hash_id")
			private Long hashId;
			@JsonProperty("is_auction")
			private Boolean isAuction;
			@JsonProperty("labels")
			private List<String> labels = new ArrayList<>();
			@JsonProperty("labelsAll")
			private List<List<String>> labelsAll = new ArrayList<>();
			@JsonProperty("labelsReleased")
			private List<List<String>> labelsReleased = new ArrayList<>();
			@JsonProperty("locality")
			private String locality;
			@JsonProperty("name")
			private String name;
			@JsonProperty("new")
			private Boolean _new;
			@JsonProperty("paid_logo")
			private Long paidLogo;
			@JsonProperty("price")
			private Long price;
			@JsonProperty("price_czk")
			private Money priceCzk;
			@JsonProperty("region_tip")
			private Long regionTip;
			@JsonProperty("rus")
			private Boolean rus;
			@JsonProperty("seo")
			private Locality seo;
			@JsonProperty("type")
			private Long type;

			@Data
			public static class EstateSummaryEmbedded {
				@JsonProperty("company")
				private Object company;
				@JsonProperty("favourite")
				private Object favourite;
				@JsonProperty("note")
				private Note note;
			}

			@Data
			public static class Gps {
				@JsonProperty("lat")
				private Double lat;
				@JsonProperty("lon")
				private Double lon;
			}
		}
	}
}
