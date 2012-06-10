package com.evinceframework.examples.petclinic;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name="OWNERS")
public class Owner extends Person {

	private String address;

	private String city;

	private String telephone;

	@Column(name="ADDRESS", length=255)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name="CITY", length=80)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name="TELEPHONE", length=20)
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
}
