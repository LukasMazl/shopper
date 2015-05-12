package com.janprach.shopper.sreality.service;

import javax.inject.Inject;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.janprach.shopper.sreality.api.Locality;

@Slf4j
@Component
public class SrealityUrlTranslationService {
	private static final String URL_CONVERT_TABLE_VALUES_JSON_FILE_NAME = "URL_CONVERT_TABLE_VALUES.json";

	private final ObjectMapper objectMapper;
	private final JsonNode urlConvertTableValues;

	@Inject
	public SrealityUrlTranslationService(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		JsonNode urlConvertTableValues;
		try {
			val is = this.getClass().getResourceAsStream("/" + URL_CONVERT_TABLE_VALUES_JSON_FILE_NAME);
			urlConvertTableValues = this.objectMapper.readTree(is);
		} catch (final Exception e) {
			log.error("Unable to read or parse {}. Try Running GenerateUrlConvertTableValuesJsonTest.",
					URL_CONVERT_TABLE_VALUES_JSON_FILE_NAME, e);
			urlConvertTableValues = NullNode.getInstance();
		}
		this.urlConvertTableValues = urlConvertTableValues;
	}

	public String getUrlString(final long srealityId, final Locality locality) {
		// http://www.sreality.cz/detail/prodej/dum/vila/praha-stresovice-na-petynce/3410497628
		// http://www.sreality.cz/detail/${category_type_cb_detail}/${category_main_cb_detail}/${category_sub_cb_detail}/${seo.locality}/${srealityId}
		val cs = this.urlConvertTableValues.get("cs");
		val categoryMainCbDetail = cs.get("category_main_cb_detail").get(locality.getCategoryMainCb()).asText();
		val categorySubCbDetail = cs.get("category_sub_cb_detail").get(locality.getCategorySubCb()).asText();
		val categoryTypeCbDetail = cs.get("category_type_cb_detail").get(locality.getCategoryTypeCb()).asText();
		return "http://www.sreality.cz/detail/" + categoryTypeCbDetail + "/" + categoryMainCbDetail + "/"
				+ categorySubCbDetail + "/" + locality.getLocality() + "/" + srealityId;
	}
}
