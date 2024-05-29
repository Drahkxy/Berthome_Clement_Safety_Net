package com.openclassrooms.safety_net.controller;

import com.openclassrooms.safety_net.model.MedicalRecord;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.update.MedicalRecordUpdate;
import com.openclassrooms.safety_net.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class MedicalRecordController {
	@Autowired
	private MedicalRecordService medicalRecordService;

	@GetMapping("medical_records")
	public ResponseEntity<Iterable<MedicalRecord>> getMedicalRecords () {
		try {
			Iterable<MedicalRecord> medicalRecords = medicalRecordService.getMedicalRecords();
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(medicalRecords);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			return ResponseEntity.notFound()
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.build();
		}
	}

	@GetMapping("medical_record")
	public ResponseEntity<MedicalRecord> getMedicalRecord (@RequestParam("first_name") final String firstName, @RequestParam("last_name") final String lastName) {
		PersonId id = new PersonId(firstName, lastName);
		try {
			MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(id);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(medicalRecord);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			return ResponseEntity.notFound()
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.build();
		}
	}

	@DeleteMapping("medical_record")
	public ResponseEntity deleteMedicalRecord (@RequestParam("first_name") final String firstName, @RequestParam	("last_name") final String lastName) {
		PersonId id = new PersonId(firstName, lastName);
		try {
			medicalRecordService.deleteMedicalRecord(id);
			return ResponseEntity.ok()
					.build();
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			return ResponseEntity.notFound()
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.build();
		}
	}

	@PostMapping("medical_record")
	public ResponseEntity<MedicalRecord> addMedicalRecord (@RequestBody MedicalRecord medicalRecord) {
		System.out.println("heeeerrreeeee");
		try {
			MedicalRecord medicalRecordAdded = medicalRecordService.addMedicalRecord(medicalRecord);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(medicalRecordAdded);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest()
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.build();
		}
	}

	@PatchMapping("medical_record")
	public ResponseEntity<MedicalRecord> updateMedicalRecord (@RequestParam("first_name") final String firstName, @RequestParam("last_name") final String lastName, @RequestBody MedicalRecordUpdate medicalRecordUpdate) {
		PersonId id = new PersonId(firstName, lastName);
		try {
			MedicalRecord medicalRecordUpdated = medicalRecordService.updateMedicalRecord(id, medicalRecordUpdate);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(medicalRecordUpdated);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			return ResponseEntity.notFound()
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.build();
		}
	}

}
