package com.janprach.shopper.sreality.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.data.rest.core.annotation.RestResource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "estate")
@Entity
public class Image extends EntityBase {
	@Column(length = 4 * 1024)
	private String description;

	@RestResource(rel = "images")
	@JsonIgnore
	@ManyToOne(optional = false)
	@JoinColumn(name = "ESTATE_ID")
//	@NaturalId
	private Estate estate;

//	@Column(name = "ord")
//	private int order;

	@Column
	private Long srealityId;

	@Column(nullable = false)
	private String url;
}
