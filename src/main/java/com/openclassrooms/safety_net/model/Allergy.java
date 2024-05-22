package com.openclassrooms.safety_net.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "allergy")
public class Allergy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	@ManyToMany(
			mappedBy = "allergies"
	)
	@JsonIgnore
	private List<MedicalRecord> medicalRecords = new ArrayList<>();


	public Allergy () {}

	public Allergy (String name) {
		this.name = name;
	}

	@Override
	public String toString () {
		return "Allergy{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
