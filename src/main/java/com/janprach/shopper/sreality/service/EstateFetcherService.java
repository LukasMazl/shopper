package com.janprach.shopper.sreality.service;

import static com.janprach.shopper.sreality.service.RichHttpClient.addNonNullParameter;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.http.client.methods.RequestBuilder;
import org.springframework.stereotype.Component;

import com.codepoetics.protonpack.StreamUtils;
import com.janprach.shopper.config.SrealityFetcherConfig;
import com.janprach.shopper.sreality.api.Estate;
import com.janprach.shopper.sreality.api.EstateListing;
import com.janprach.shopper.sreality.api.EstateListing.EstateListingEmbedded.EstateSummary;
import com.janprach.shopper.sreality.service.RichHttpClient.EntityWithRawResponse;

import lombok.AllArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
@Component
public class EstateFetcherService {
	private final RichHttpClient richHttpClient;
	private final SrealityFetcherConfig srealityFetcherConfig;

	public Stream<EstateSummary> fetchEstateSummaries() {
		Stream<Optional<EstateListing>> pagingEstateListingStream = IntStream
				.range(1, srealityFetcherConfig.getMaxEstateSummaryPageNumber())
				.mapToObj(page -> fetchEstateListing(page, srealityFetcherConfig.getEstateSummariesPerPage()));
		Stream<Optional<EstateListing>> estateListings = StreamUtils.takeWhile(pagingEstateListingStream,
				el -> el.isPresent() && !el.get().getEmbedded().getEstates().isEmpty());
		return estateListings.flatMap(el -> el.get().getEmbedded().getEstates().stream());
	}

	public Optional<EstateListing> fetchEstateListing(final int page, final int perPage) {
		log.debug("Fetching estate listing page {} (x{}) ...", page, perPage);
		val sanitizedPage = Math.max(1, page);
		val sanitizedPerPage = Math.max(10, Math.min(60, perPage));
		val rb = RequestBuilder.get(srealityFetcherConfig.getEstateApiUri());
		addNonNullParameter(rb, "category_main_cb", srealityFetcherConfig.getCategoryMain());
		addNonNullParameter(rb, "category_sub_cb", srealityFetcherConfig.getCategorySub());
		addNonNullParameter(rb, "category_type_cb", srealityFetcherConfig.getCategoryType());
		addNonNullParameter(rb, "locality_district_id", srealityFetcherConfig.getLocalityDistrict());
		addNonNullParameter(rb, "locality_region_id", srealityFetcherConfig.getLocalityRegion());
		addNonNullParameter(rb, "czk_price_summary_order2", srealityFetcherConfig.getPriceRange());
		addNonNullParameter(rb, "per_page", Integer.toString(sanitizedPerPage));
		addNonNullParameter(rb, "page", Integer.toString(sanitizedPage));
		val httpRequest = rb.build();
		return this.richHttpClient.getEntity(httpRequest, EstateListing.class);
	}

	public Optional<Estate> fetchEstate(final long estateHashId) {
		log.debug("Fetching estate id {} ...", estateHashId);
		val httpRequest = RequestBuilder.get(srealityFetcherConfig.getEstateApiUri() + "/" + estateHashId).build();
		return this.richHttpClient.getEntity(httpRequest, Estate.class);
	}

	public Optional<EntityWithRawResponse<Estate>> fetchEstateWithRawResponse(final long estateHashId) {
		log.debug("Fetching estate id {} ...", estateHashId);
		val httpRequest = RequestBuilder.get(srealityFetcherConfig.getEstateApiUri() + "/" + estateHashId).build();
		return this.richHttpClient.getEntityWithRawResponse(httpRequest, Estate.class);
	}
}
