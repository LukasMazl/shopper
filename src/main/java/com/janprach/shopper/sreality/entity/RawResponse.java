package com.janprach.shopper.sreality.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
@Table(indexes = { @Index(name = "RAW_RESPONSE_BY_ESTATE_ID", columnList = "ESTATE_ID") })
public class RawResponse extends EntityBase {
	@JsonIgnore
	@ManyToOne(optional = false)
	@JoinColumn(name = "ESTATE_ID")
//	@NaturalId
	private Estate estate;

	@Column(length = 1 * 1024 * 1024, nullable = false)
	private String rawResponseString;
}
