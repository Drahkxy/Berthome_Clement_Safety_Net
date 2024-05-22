package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Medication;
import com.openclassrooms.safety_net.repository.MedicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class MedicationService {
	@Autowired
	private MedicationRepository medicationRepository;

	public Medication getMedicationByNameAndDosage (String name, int dosage) {
		Optional<Medication> medication = medicationRepository.findByNameAndDosage(name, dosage);
		return medication.orElse(null);
	}

	public List<Medication> getMedicationsOnlyWithoutMedicalRecordLink () {
		Iterable<Medication> medications = medicationRepository.findAllergiesOnlyWithNoLinks();
		return StreamSupport.stream(medications.spliterator(), false).toList();
	}

	public void deleteMedication (Medication medication) {
		medicationRepository.delete(medication);
	}

	public void deleteMedicationsWithoutMedicalRecordLink () {
		List<Medication> medications = getMedicationsOnlyWithoutMedicalRecordLink();
		medications.forEach(this::deleteMedication);
	}
}
