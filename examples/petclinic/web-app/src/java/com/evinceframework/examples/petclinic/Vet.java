package com.evinceframework.examples.petclinic;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity(name="VETS")
public class Vet extends Person {

	private Set<Specialty> specialties = new HashSet<Specialty>();

	@ManyToMany(targetEntity=Specialty.class)
	@JoinTable(name="VET_SPECIALTIES", 
		joinColumns=@JoinColumn(name="VET_ID"), 
		inverseJoinColumns=@JoinColumn(name="SPECIALTY_ID"))
	public Set<Specialty> getSpecialties() {
		return this.specialties;
	}
	
	protected void setSpecialties(Set<Specialty> specialties) {
		this.specialties = specialties;
	}
	
}
