package com.janprach.shopper.sreality.experimental;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "pagedEntities")
public class SeznamPagedResources<T> extends Resources<T> {
	public static SeznamPagedResources<?> NO_PAGE = new SeznamPagedResources<Object>();

	@XmlAttribute
	@JsonProperty("page")
	private int page;
	@XmlAttribute
	@JsonProperty("per_page")
	private int perPage;

	protected SeznamPagedResources() {
		this(new ArrayList<T>(), 0, 10);
	}

	public SeznamPagedResources(Collection<T> content, int page, int perPage, Link... links) {
		this(content, page, perPage, Arrays.asList(links));
	}

	public SeznamPagedResources(Collection<T> content, int page, int perPage, Iterable<Link> links) {
		super(content, links);
		this.page = page;
		this.perPage = perPage;
	}

	@JsonProperty("page")
	public int getPage() {
		return this.page;
	}

	@JsonProperty("per_page")
	public int getPerPage() {
		return this.perPage;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Resource<S>, S> SeznamPagedResources<T> wrap(Iterable<S> content, int page, int perPage) {
		Assert.notNull(content, "content cannot be null");
		ArrayList<T> resources = new ArrayList<T>();

		for (S element : content) {
			resources.add((T) new Resource<S>(element));
		}

		return new SeznamPagedResources<T>(resources, page, perPage);
	}

	@JsonIgnore
	public Link getNextLink() {
		return getLink(Link.REL_NEXT);
	}

	@JsonIgnore
	public Link getPreviousLink() {
		return getLink(Link.REL_PREVIOUS);
	}

	@Override
	public String toString() {
		return String.format("SeznamPagedResource { content: %s, page: %s, perPage: %s, links: %s }", getContent(),
				page, perPage, getLinks());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || !getClass().equals(obj.getClass())) {
			return false;
		}

		SeznamPagedResources<?> that = (SeznamPagedResources<?>) obj;
		boolean seznamPagedEquals = this.page == that.page && this.perPage == that.perPage;

		return seznamPagedEquals ? super.equals(obj) : false;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result += 31 * (int) (this.page ^ this.page >>> 32);
		result += 31 * (int) (this.perPage ^ this.perPage >>> 32);
		return result;
	}
}
