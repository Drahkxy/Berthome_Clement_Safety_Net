package com.openclassrooms.safety_net.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "address")
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String label;

	private String zip;

	private String city;

	@OneToMany(
			cascade = {
					CascadeType.MERGE,
					CascadeType.PERSIST
			},
			fetch = FetchType.LAZY,
			orphanRemoval = true,
			mappedBy = "address"
	)
	private List<Person> residents = new ArrayList<>();

	@OneToMany(
			cascade = {
					CascadeType.MERGE,
					CascadeType.PERSIST
			},
			fetch = FetchType.LAZY,
			orphanRemoval = true,
			mappedBy = "address"
	)
	private List<FireStation> fireStations = new ArrayList<>();


	public Address (String label, String zip, String city) {
		this.label = label;
		this.zip = zip;
		this.city = city;
	}


	public void addResident(Person person) {
		residents.add(person);
		person.setAddress(this);
	}

	public void removeResident(Person person) {
		residents.remove(person);
		person.setAddress(null);
	}

	public void addFireStation(FireStation fireStation) {
		fireStations.add(fireStation);
		fireStation.setAddress(this);
	}

	public void removeFireStation(FireStation fireStation) {
		fireStations.remove(fireStation);
		fireStation.setAddress(null);
	}

	@Override
	public String toString () {
		return "Address{" +
				"id=" + id +
				", label='" + label + '\'' +
				", zip='" + zip + '\'' +
				", city='" + city + '\'' +
				'}';
	}
}
