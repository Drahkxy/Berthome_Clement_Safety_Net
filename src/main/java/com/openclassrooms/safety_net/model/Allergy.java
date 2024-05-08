package com.openclassrooms.safety_net.model;

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
	private List<Person> persons = new ArrayList<>();

	public Allergy (String name) {
		this.name = name;
	}
}
