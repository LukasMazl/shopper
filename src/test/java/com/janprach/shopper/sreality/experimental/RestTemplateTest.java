package com.janprach.shopper.sreality.experimental;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.janprach.shopper.sreality.api.Estate;
import com.janprach.shopper.sreality.api.EstateListing.EstateListingEmbedded.EstateSummary;

import lombok.val;

@org.junit.Ignore("Nope, seznam is not using Spring's implementation - we have to have custom.")
public class RestTemplateTest {
	private static final String ESTATES_URL = "http://www.sreality.cz/api/cs/v1/estates";

	@Test
	public void testFetchEstateSummariesUsingRestTemplate() {
		val restTemplate = this.restTemplate();
		val estateUrl = ESTATES_URL + "?per_page={per_page}&page={page}";
		val estateTypeRef = new ParameterizedTypeReference<SeznamPagedResources<EstateSummary>>() {
		};
		val responseEntity = restTemplate.exchange(estateUrl, HttpMethod.GET, null, estateTypeRef, 20, 0);
		val resources = responseEntity.getBody();
		assertThat(resources.getPerPage()).isEqualTo(20);
		val estateSummaries = new ArrayList<EstateSummary>(resources.getContent());
		for (val estateSummary : estateSummaries) {
			System.out.println("name = " + estateSummary.getName());
		}

		// val estate = restTemplate.getForObject(ESTATES_URL + "/{srealityId}",
		// Estate.class, estateSummaries.get(0).getHashId());
		// System.out.println(estate);
	}

	@Test
	public void testFetchEstateUsingRestTemplate() {
		val restTemplate = this.restTemplate();
		val estateUrl = ESTATES_URL + "/{srealityId}";
		val estateTypeRef = new ParameterizedTypeReference<Resource<Estate>>() {
		};
		val responseEntity = restTemplate.exchange(estateUrl, HttpMethod.GET, null, estateTypeRef, 2693136476L);
		val resource = responseEntity.getBody();
		assertThat(resource.getId().getRel()).isEqualTo("self");
		assertThat(resource.getLink("self").getHref()).startsWith(ESTATES_URL);
		val estate = resource.getContent();
		System.out.println("name = " + estate.getName());
	}

	private RestTemplate restTemplate() {
		val mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new Jackson2HalModule());
		val converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
		converter.setObjectMapper(mapper);
		return new RestTemplate(Arrays.asList(converter));
	}
}
