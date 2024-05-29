package com.openclassrooms.safety_net.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "address")
@DynamicUpdate
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
			orphanRemoval = false,
			mappedBy = "address"
	)
	@JsonIgnore
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
	@JsonIgnore
	private List<FireStation> fireStations = new ArrayList<>();


	public Address () {}

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
		return "{ \"id\": %d, \"label\": %s, \"zip\": %s, \"city\": %s }".formatted(id, label, zip, city);
	}
}
