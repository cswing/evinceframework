package com.evinceframework.examples.petclinic;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Simple JavaBean domain object with an id property.
 * Used as a base class for objects needing this property.
 * 
 * copied from org.springframework.samples.petclinic
 */
@MappedSuperclass
public class BaseEntity {

	private Integer id;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	protected void setId(Integer id) {
		this.id = id;
	}

	@Transient
	public boolean isNew() {
		return (this.id == null);
	}
	
}
