package com.openclassrooms.safety_net.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "medication")
public class Medication {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	private int dosage;

	@ManyToMany(
			mappedBy = "medications"
	)
	private List<Person> patients = new ArrayList<>();

	public Medication (String name, int dosage) {
		this.name = name;
		this.dosage = dosage;
	}
}
