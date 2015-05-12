package com.janprach.shopper.sreality.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import lombok.Data;

@Data
@MappedSuperclass
public class EntityBase {
	@Id
	@GeneratedValue
	protected long id;

	@Column(name = "created_at")
	protected Date createdAt;

	@Column(name = "updated_at")
	protected Date updatedAt;

	@Version
	protected int version;

	@PrePersist
	void updateCreatedAt() {
		this.createdAt = this.updatedAt = new Date();
	}

	@PreUpdate
	void updateUpdatedAt() {
		this.updatedAt = new Date();
	}

	public void setEntityBaseFieldsForUpdate(final EntityBase that) {
		this.setId(that.getId());
		this.setCreatedAt(that.getCreatedAt());
		this.setUpdatedAt(that.getUpdatedAt());
		this.setVersion(that.getVersion());
	}
}
