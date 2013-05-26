package com.evinceframework.jpa;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

@MappedSuperclass
public class BaseEntity {

	private Integer id;

	private String identifier = UUID.randomUUID().toString();
	
	private Integer version = 0;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	protected void setId(Integer id) {
		this.id = id;
	}

	@Column(name="oid", unique = true, nullable = false)
	public String getObjectIdentifier() {
		return identifier;
	}

	protected void setObjectIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Version
	@Column(name="version")
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Transient
	public boolean isNew() {
		return (this.id == null);
	}

}
