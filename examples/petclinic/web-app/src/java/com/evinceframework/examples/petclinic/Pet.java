package com.evinceframework.examples.petclinic;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="PETS")
public class Pet extends NamedEntity {

	private Date birthDate;

	private PetType type;

	private Owner owner;

	@Column(name="BIRTH_DATE")
	public Date getBirthDate() {
		return this.birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	@ManyToOne()
	@JoinColumn(name="TYPE_ID", nullable=false)
	public PetType getType() {
		return this.type;
	}

	public void setType(PetType type) {
		this.type = type;
	}

	@ManyToOne()
	@JoinColumn(name="OWNER_ID", nullable=false)
	public Owner getOwner() {
		return this.owner;
	}
	
	public void setOwner(Owner owner) {
		this.owner = owner;
	}
	
}
