package com.janprach.shopper.sreality.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(indexes = { @Index(name = "ESTATE_BY_TITLE", columnList = "TITLE") },
		uniqueConstraints = { @UniqueConstraint(name = "ESTATE_BY_SREALITY_ID", columnNames = "SREALITYID") })
public class Estate extends EntityBase {
	@Column
	private Integer areaBuild;

	@Column
	private Integer areaFloor;

	@Column
	private Integer areaGarden;

	@Column
	private Integer areaTotal;

	@Column
	private Integer areaUsable;

	@Column
	private String address;

	@Column(length = 64 * 1024)
	private String description;

	@Column
	private Double latitude;

	@Column
	private Double longitude;

	@Column(length = 1 * 1024)
	private String metaDescription;

	@Column
	private Long price;

	@Column(nullable = false)
	private Long srealityId;

	@Column
	private String state;

	@Column(nullable = false)
	private String title;

	@Column
	private String url;

	@Column
	private Integer zoom;

	@Column
	protected Date dateSort;

	@Column
	private Boolean active = true;

	@Column
	private Boolean visible = true;

	@Column
	private Integer stars = 0;

	@Column(nullable = false)
	private Long duplicityId = 0L;

	@Column
	private String note;

	@OneToMany(mappedBy = "estate")
	private List<Image> images = new ArrayList<Image>();

	@OneToMany(mappedBy = "estate")
	private List<RawResponse> rawResponses = new ArrayList<RawResponse>();

	@OneToMany(mappedBy = "estate")
	@OrderBy("createdAt ASC")
	private List<History> histories = new ArrayList<History>();

	// @Enumerated(EnumType.ORDINAL)
	// @Temporal(TemporalType.DATE)
}
