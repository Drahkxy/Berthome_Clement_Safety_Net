package com.openclassrooms.safety_net.util.json.model;

import lombok.Data;

@Data
public class MedicalRecordJson {
	private String firstName;
	private String lastName;
	private String birthdate;
	private String[] medications;
	private String[] allergies;
}
