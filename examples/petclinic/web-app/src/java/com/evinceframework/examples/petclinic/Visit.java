package com.evinceframework.examples.petclinic;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="VISITS")
public class Visit extends BaseEntity {

	private Date date;

	private String description;

	private Pet pet;


	/** Creates a new instance of Visit for the current date */
	public Visit() {
		this.date = new Date();
	}

	@Column(name="VISIT_DATE")
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name="DESCRIPTION", length=255)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne()
	@JoinColumn(name="PET_ID", nullable=false)
	public Pet getPet() {
		return this.pet;
	}

	public void setPet(Pet pet) {
		this.pet = pet;
	}

}
