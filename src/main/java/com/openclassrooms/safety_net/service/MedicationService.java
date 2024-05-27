package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Medication;
import com.openclassrooms.safety_net.repository.MedicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
		Iterable<Medication> medications = medicationRepository.findMedicationsOnlyWithNoLinks();
		return StreamSupport.stream(medications.spliterator(), false).toList();
	}

	public void deleteMedication (Medication medication) throws ResponseStatusException {
		if (medicationRepository.existsById(medication.getId())) {
			medicationRepository.delete(medication);
		} else {
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND,
					"Medication %s whit dosage %dmg not found and can't be deleted.".formatted(
							medication.getName(),
							medication.getDosage()
					)
			);
		}
	}

	public void deleteMedicationsWithoutMedicalRecordLink () {
		List<Medication> medications = getMedicationsOnlyWithoutMedicalRecordLink();
		try {
			medications.forEach(this::deleteMedication);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
		}
	}

}
