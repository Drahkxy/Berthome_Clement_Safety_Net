package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Allergy;
import com.openclassrooms.safety_net.model.MedicalRecord;
import com.openclassrooms.safety_net.model.Medication;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.update.MedicalRecordUpdate;
import com.openclassrooms.safety_net.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordService {
	@Autowired
	private MedicalRecordRepository medicalRecordRepository;

	@Autowired
	private AllergyService allergyService;

	@Autowired
	private MedicationService medicationService;

	public MedicalRecord getMedicalRecordById (PersonId personId) throws ResponseStatusException {
		Optional<MedicalRecord> medicalRecord = medicalRecordRepository.findById(personId);
		if (medicalRecord.isPresent()) {
			return medicalRecord.get();
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No medical record found for %s %s.".formatted(personId.getFirstName(), personId.getFirstName()));
		}
	}

	public Iterable<MedicalRecord> getMedicalRecords () throws ResponseStatusException {
		Iterable<MedicalRecord> medicalRecords = medicalRecordRepository.findAll();
		if (medicalRecords.iterator().hasNext()) {
			return medicalRecords;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No medical record found");
		}
	}

	public void deleteMedicalRecord (PersonId personId) throws ResponseStatusException {
		if (medicalRecordRepository.existsById(personId)) {
			medicalRecordRepository.deleteById(personId);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossible to delete medical record for %s %s.".formatted(personId.getFirstName(), personId.getFirstName()));
		}
	}

	public MedicalRecord addMedicalRecord (MedicalRecord medicalRecord) throws ResponseStatusException {
		PersonId id = new PersonId(medicalRecord.getFirstName(), medicalRecord.getLastName());
		if (!medicalRecordRepository.existsById(id)) {
			List<Allergy> allergies = medicalRecord.getAllergies();
			allergies = allergies.stream().map(allergy -> {
				Allergy existingAllergy = allergyService.getAllergyByName(allergy.getName());
				if (existingAllergy != null) {
					return existingAllergy;
				}
				return allergy;
			}).toList();
			medicalRecord.setAllergies(allergies);

			List<Medication> medications = medicalRecord.getMedications();
			medications = medications.stream().map(medication -> {
				Medication existingMedication = medicationService.getMedicationByNameAndDosage(medication.getName(), medication.getDosage());
				if (existingMedication != null) {
					return existingMedication;
				}
				return medication;
			}).toList();
			medicalRecord.setMedications(medications);

			return medicalRecordRepository.save(medicalRecord);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medical record for %s %s already exist.".formatted(medicalRecord.getFirstName(), medicalRecord.getLastName()));
		}
	}

	public MedicalRecord updateMedicalRecord (PersonId id, MedicalRecordUpdate medicalRecordUpdate) throws ResponseStatusException {
		MedicalRecord medicalRecord = getMedicalRecordById(id);

		List<Allergy> allergies = medicalRecordUpdate.getAllergies();
		System.out.println(allergies.size());
		allergies = allergies.stream().map(allergy -> {
			Allergy existingAllergy = allergyService.getAllergyByName(allergy.getName());
			if (existingAllergy != null) {
				return existingAllergy;
			}
			return allergy;
		}).toList();
		medicalRecord.setAllergies(allergies);

		List<Medication> medications = medicalRecordUpdate.getMedications();
		medications = medications.stream().map(medication -> {
			Medication existingMedication = medicationService.getMedicationByNameAndDosage(medication.getName(), medication.getDosage());
			if (existingMedication != null) {
				return existingMedication;
			}
			return medication;
		}).toList();
		medicalRecord.setMedications(medications);

		medicalRecord = medicalRecordRepository.save(medicalRecord);

		allergyService.deleteAllergiesWithoutMedicalRecordLink();
		medicationService.deleteMedicationsWithoutMedicalRecordLink();

		return medicalRecord;
	}

}
