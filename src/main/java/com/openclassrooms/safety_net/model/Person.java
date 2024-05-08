package com.openclassrooms.safety_net.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "person")
public class Person {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	private String phone;

	private String email;

	private LocalDate birthday;


	@ManyToOne(
			cascade = {
					CascadeType.MERGE,
					CascadeType.PERSIST
			}
	)
	@JoinColumn(name = "address_id")
	private Address address;


	@ManyToMany(
			fetch = FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE
			}
	)
	@JoinTable(
			name = "person_medication",
			joinColumns = @JoinColumn(name = "person_id"),
			inverseJoinColumns = @JoinColumn(name = "medication_id")
	)
	private List<Medication> medications = new ArrayList<>();


	@ManyToMany(
			fetch = FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE
			}
	)
	@JoinTable(
			name = "person_allergy",
			joinColumns = @JoinColumn(name = "person_id"),
			inverseJoinColumns = @JoinColumn(name = "allergy_id")
	)
	private List<Allergy> allergies = new ArrayList<>();


	public Person (String firstName, String lastName, String phone, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.email = email;
	}

	public void addMedication(Medication medication) {
		medications.add(medication);
		medication.getPatients().add(this);
	}

	public void removeMedication(Medication medication) {
		medications.remove(medication);
		medication.getPatients().remove(this);
	}

	public void addAllergy(Allergy allergy) {
		allergies.add(allergy);
		allergy.getPersons().add(this);
	}

	public void removeAllergy(Allergy allergy) {
		allergies.remove(allergy);
		allergy.getPersons().remove(this);
	}

}
