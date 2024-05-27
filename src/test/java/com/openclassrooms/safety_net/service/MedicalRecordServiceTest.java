package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Allergy;
import com.openclassrooms.safety_net.model.MedicalRecord;
import com.openclassrooms.safety_net.model.Medication;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.update.MedicalRecordUpdate;
import com.openclassrooms.safety_net.repository.MedicalRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MedicalRecordServiceTest {
	@Mock
	private MedicalRecordRepository medicalRecordRepository;
	@Mock
	private AllergyService allergyService;
	@Mock
	private MedicationService medicationService;
	@InjectMocks
	private MedicalRecordService medicalRecordService;

	private MedicalRecord existingMedicalRecord;
	private MedicalRecord nonExistentMedicalRecord;
	private PersonId existingId;
	private PersonId nonExistentId;

	@BeforeEach
	public void setUp () {
		existingMedicalRecord = new MedicalRecord(
				"John",
				"Doe",
				LocalDate.of(	1996, 3, 9)
		);
		existingMedicalRecord.setMedications(List.of(new Medication("aznol", 100)));
		existingMedicalRecord.setAllergies(List.of(new Allergy("peanut")));

		existingId = new PersonId(existingMedicalRecord.getFirstName(), existingMedicalRecord.getLastName());

		nonExistentMedicalRecord = new MedicalRecord(
				"Jane",
				"Doe",
				LocalDate.of(1997, 6, 23)
		);
		nonExistentMedicalRecord.setMedications(List.of(new Medication("pharmacol", 150)));
		nonExistentMedicalRecord.setAllergies(List.of(new Allergy("pineApple")));

		nonExistentId = new PersonId(nonExistentMedicalRecord.getFirstName(), nonExistentMedicalRecord.getLastName());
	}

	@Test
	public void getMedicalRecordById_existingMedicalRecord_test () {
		when(medicalRecordRepository.findById(existingId)).thenReturn(Optional.of(existingMedicalRecord));

		MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(existingId);

		assertEquals(existingMedicalRecord, medicalRecord);

		verify(medicalRecordRepository, times(1)).findById(existingId);
	}

	@Test
	public void getMedicalRecordById_nonExistentMedicalRecord_test () {
		when(medicalRecordRepository.findById(nonExistentId)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			medicalRecordService.getMedicalRecordById(nonExistentId);
		});

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

		verify(medicalRecordRepository, times(1)).findById(nonExistentId);
	}

	@Test
	public void getMedicalRecords_notEmptyList_test () {
		List<MedicalRecord> medicalRecords = List.of(existingMedicalRecord);

		when(medicalRecordRepository.findAll()).thenReturn(medicalRecords);

		List<MedicalRecord> mr = StreamSupport.stream(medicalRecordService.getMedicalRecords().spliterator(), false)
				.toList();

		assertEquals(medicalRecords, mr);
		assertEquals(medicalRecords.get(0), mr.get(0));

		verify(medicalRecordRepository, times(1)).findAll();
	}

	@Test
	public void getMedicalRecords_emptyList_test () {
		List<MedicalRecord> medicalRecords = List.of();

		when(medicalRecordRepository.findAll()).thenReturn(medicalRecords);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			medicalRecordService.getMedicalRecords();
		});

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

		verify(medicalRecordRepository, times(1)).findAll();
	}

	@Test
	public void deleteMedicalRecord_existingMedicalRecord_test () {
		when(medicalRecordRepository.existsById(existingId)).thenReturn(true);

		medicalRecordService.deleteMedicalRecord(existingId);

		verify(medicalRecordRepository, times(1)).existsById(existingId);
		verify(medicalRecordRepository, times(1)).deleteById(existingId);
	}

	@Test
	public void deleteMedicalRecord_nonExistentMedicalRecord_test () {
		when(medicalRecordRepository.existsById(nonExistentId)).thenReturn(false);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			medicalRecordService.deleteMedicalRecord(nonExistentId);
		});

		verify(medicalRecordRepository, times(1)).existsById(nonExistentId);
		verify(medicalRecordRepository, never()).deleteById(any(PersonId.class));
	}

	@Test
	public void addMedicalRecord_success_test () {
		when(medicalRecordRepository.existsById(nonExistentId)).thenReturn(false);

		Allergy allergy = nonExistentMedicalRecord.getAllergies().get(0);
		when(allergyService.getAllergyByName(allergy.getName()))
				.thenReturn(allergy);

		Medication medication = nonExistentMedicalRecord.getMedications().get(0);
		when(medicationService.getMedicationByNameAndDosage(medication.getName(), medication.getDosage()))
				.thenReturn(medication);

		when(medicalRecordRepository.save(nonExistentMedicalRecord)).thenReturn(nonExistentMedicalRecord);

		MedicalRecord medicalRecord = medicalRecordService.addMedicalRecord(nonExistentMedicalRecord);

		assertEquals(nonExistentMedicalRecord, medicalRecord);

		verify(medicalRecordRepository, times(1)).existsById(nonExistentId);
		verify(allergyService, times(1)).getAllergyByName(allergy.getName());
		verify(medicationService, times(1))
				.getMedicationByNameAndDosage(medication.getName(), medication.getDosage());
		verify(medicalRecordRepository, times(1)).save(nonExistentMedicalRecord);
	}

	@Test
	public void addMedicalRecord_alreadyExist_test () {
		when(medicalRecordRepository.existsById(existingId)).thenReturn(true);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			medicalRecordService.addMedicalRecord(existingMedicalRecord);
		});

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

		verify(medicalRecordRepository, times(1)).existsById(existingId);
		verify(allergyService, never()).getAllergyByName(anyString());
		verify(medicationService, never()).getMedicationByNameAndDosage(anyString(), anyInt());
		verify(medicalRecordRepository, never()).save(any(MedicalRecord.class));
	}

	@Test
	public void updateMedicalRecord_existingFireStation_test () {
		when(medicalRecordRepository.findById(existingId)).thenReturn(Optional.of(existingMedicalRecord));

		Allergy allergy = existingMedicalRecord.getAllergies().get(0);
		when(allergyService.getAllergyByName(allergy.getName()))
				.thenReturn(allergy);

		Medication medication = existingMedicalRecord.getMedications().get(0);
		when(medicationService.getMedicationByNameAndDosage(medication.getName(), medication.getDosage()))
				.thenReturn(medication);

		when(medicalRecordRepository.save(existingMedicalRecord)).thenReturn(existingMedicalRecord);

		MedicalRecordUpdate medicalRecordUpdate = new MedicalRecordUpdate();
		medicalRecordUpdate.setAllergies(existingMedicalRecord.getAllergies());
		medicalRecordUpdate.setMedications(existingMedicalRecord.getMedications());

		MedicalRecord medicalRecord = medicalRecordService.updateMedicalRecord(existingId, medicalRecordUpdate);

		assertEquals(existingMedicalRecord, medicalRecord);

		verify(medicalRecordRepository, times(1)).findById(existingId);
		verify(allergyService, times(1)).getAllergyByName(allergy.getName());
		verify(medicationService, times(1))
				.getMedicationByNameAndDosage(medication.getName(), medication.getDosage());
		verify(medicalRecordRepository, times(1)).save(existingMedicalRecord);
		verify(allergyService, times(1)).deleteAllergiesWithoutMedicalRecordLink();
		verify(medicationService, times(1)).deleteMedicationsWithoutMedicalRecordLink();
	}

	@Test
	public void updateMedicalRecord_nonExistentFireStation_test () {
		when(medicalRecordRepository.findById(nonExistentId)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			medicalRecordService.updateMedicalRecord(nonExistentId, new MedicalRecordUpdate());
		});

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

		verify(medicalRecordRepository, times(1)).findById(nonExistentId);
		verify(allergyService, never()).getAllergyByName(anyString());
		verify(medicationService, never()).getMedicationByNameAndDosage(anyString(), anyInt());
		verify(medicalRecordRepository, never()).save(any(MedicalRecord.class));
		verify(allergyService, never()).deleteAllergiesWithoutMedicalRecordLink();
		verify(medicationService, never()).deleteMedicationsWithoutMedicalRecordLink();
	}

}
