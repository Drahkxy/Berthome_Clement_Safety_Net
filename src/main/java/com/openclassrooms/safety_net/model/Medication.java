package com.openclassrooms.safety_net.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	@JsonIgnore
	private List<MedicalRecord> medicalRecords = new ArrayList<>();


	public Medication () {}

	public Medication (String name, int dosage) {
		this.name = name;
		this.dosage = dosage;
	}

	@Override
	public String toString () {
		return "Medication{" +
				"id=" + id +
				", name='" + name + '\'' +
				", dosage=" + dosage +
				'}';
	}
}
