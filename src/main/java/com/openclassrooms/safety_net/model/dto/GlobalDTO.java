package com.openclassrooms.safety_net.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class GlobalDTO {
	private List<PersonDTO> persons;
	private List<FireStationDTO> firestations;
	private List<MedicalRecordDTO> medicalrecords;
}
