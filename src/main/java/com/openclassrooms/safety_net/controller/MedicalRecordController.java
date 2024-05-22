package com.openclassrooms.safety_net.controller;

import com.openclassrooms.safety_net.model.MedicalRecord;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.update.MedicalRecordUpdate;
import com.openclassrooms.safety_net.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class MedicalRecordController {
	@Autowired
	private MedicalRecordService medicalRecordService;

	@GetMapping("medical_record/{first_name}&{last_name}")
	public MedicalRecord getMedicalRecord (@PathVariable("first_name") final String firstName, @PathVariable("last_name") final String lastName) {
		PersonId id = new PersonId(firstName, lastName);
		try {
			return medicalRecordService.getMedicalRecordById(id);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error occurred while retrieving medical record of %s %s.".formatted(firstName, lastName));
		}
	}

	@GetMapping("medical_records")
	public Iterable<MedicalRecord> getMedicalRecords () {
		try {
			return medicalRecordService.getMedicalRecords();
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error occurred while retrieving medical records.");
		}
	}

	@DeleteMapping("medical_record/{first_name}&{last_name}")
	public void deleteMedicalRecord (@PathVariable("first_name") final String firstName, @PathVariable("last_name") final String lastName) {
		PersonId id = new PersonId(firstName, lastName);
		try {
			medicalRecordService.deleteMedicalRecord(id);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error occurred while deleting medical record of %s %s.".formatted(firstName, lastName));
		}
	}

	@PutMapping("medical_record")
	public MedicalRecord addMedicalRecord (@RequestBody MedicalRecord medicalRecord) {
		try {
			return medicalRecordService.addMedicalRecord(medicalRecord);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error occurred while adding medical record for %s %s.".formatted(medicalRecord.getFirstName(), medicalRecord.getLastName()));
		}
	}

	@PatchMapping("medical_record/{first_name}&{last_name}")
	public MedicalRecord updateMedicalRecord (@PathVariable("first_name") final String firstName, @PathVariable("last_name") final String lastName, @RequestBody MedicalRecordUpdate medicalRecordUpdate) {
		PersonId id = new PersonId(firstName, lastName);
		try {
			return medicalRecordService.updateMedicalRecord(id, medicalRecordUpdate);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error occurred while updating medical record of %s %s".formatted(firstName, lastName));
		}
	}

}
