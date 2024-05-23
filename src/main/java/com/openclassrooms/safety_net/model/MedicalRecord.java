package com.openclassrooms.safety_net.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medical_record")
@Data
@IdClass(PersonId.class)
@DynamicUpdate
public class MedicalRecord {
	@Id
	@Column(name = "first_name")
	private String firstName;

	@Id
	@Column(name = "last_name")
	private String lastName;

	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate birthday;

	@ManyToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.EAGER
	)
	@JoinTable(
			name = "medical_record_medication",
			joinColumns = {
					@JoinColumn(name = "first_name"),
					@JoinColumn(name = "last_name")
			},
			inverseJoinColumns = @JoinColumn(name = "medication_id")
	)
	private List<Medication> medications = new ArrayList<>();

	public void addMedication (Medication medication) {
		medications.add(medication);
		medication.getMedicalRecords().add(this);
	}

	public void removeMedication (Medication medication) {
		medications.remove(medication);
		medication.getMedicalRecords().remove(this);
	}


	@ManyToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.EAGER
	)
	@JoinTable(
			name = "medical_record_allergy",
			joinColumns = {
					@JoinColumn(name = "first_name"),
					@JoinColumn(name = "last_name")
			},
			inverseJoinColumns = @JoinColumn(name = "allergy_id")
	)
	private List<Allergy> allergies = new ArrayList<>();

	public void addAllergy (Allergy allergy) {
		allergies.add(allergy);
		allergy.getMedicalRecords().add(this);
	}

	public void removeAllergy (Allergy allergy) {
		allergies.remove(allergy);
		allergy.getMedicalRecords().remove(this);
	}


	public int getAge () {
		Period period = Period.between(this.birthday, LocalDate.now());
		return period.getYears();
	}


	public MedicalRecord () {
	}

	public MedicalRecord (String firstName, String lastName, LocalDate birthday) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
	}
}
