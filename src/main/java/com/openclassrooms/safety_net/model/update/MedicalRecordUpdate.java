package com.openclassrooms.safety_net.model.update;

import com.openclassrooms.safety_net.model.Allergy;
import com.openclassrooms.safety_net.model.Medication;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MedicalRecordUpdate {
	private LocalDate birthday;

	private List<Medication> medications;

	private List<Allergy> allergies;
}
