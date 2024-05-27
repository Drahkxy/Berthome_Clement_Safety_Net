package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Medication;
import com.openclassrooms.safety_net.repository.MedicationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MedicationServiceTest {
	@Mock
	private MedicationRepository medicationRepository;

	@InjectMocks
	private MedicationService medicationService;

	private static Medication existingMedication;
	private static Medication nonExistentMedication;
	private static List<Medication> medications;

	@BeforeAll
	public static void setUpStatic () {
		existingMedication = new Medication("aznol", 100);
		existingMedication.setId(1);
		nonExistentMedication = new Medication("pharmacol", 150);
		medications = List.of(existingMedication);
	}

	@BeforeEach
	public void setUp () {
		when(
				medicationRepository.findByNameAndDosage(
						existingMedication.getName(),
						existingMedication.getDosage()
				)
		).thenReturn(Optional.of(existingMedication));

		when(
				medicationRepository.findByNameAndDosage(
						nonExistentMedication.getName(),
						nonExistentMedication.getDosage()
				)
		).thenReturn(Optional.empty());

		when(medicationRepository.existsById(existingMedication.getId())).thenReturn(true);
		when(medicationRepository.existsById(nonExistentMedication.getId())).thenReturn(false);

		when(medicationRepository.findMedicationsOnlyWithNoLinks()).thenReturn(medications);
	}

	@Test
	public void getMedicationByNameAndDosage_existingAllergy_test () {
		Medication medication = medicationService.getMedicationByNameAndDosage(
				existingMedication.getName(),
				existingMedication.getDosage()
		);

		assertEquals(existingMedication, medication);

		verify(medicationRepository, times(1)).findByNameAndDosage(
				existingMedication.getName(),
				existingMedication.getDosage()
		);
	}

	@Test
	public void getMedicationByNameAndDosage_nonExistentAllergy_test () {
		Medication medication = medicationService.getMedicationByNameAndDosage(
				nonExistentMedication.getName(),
				nonExistentMedication.getDosage()
		);

		assertNull(medication);

		verify(medicationRepository, times(1)).findByNameAndDosage(
				nonExistentMedication.getName(),
				nonExistentMedication.getDosage()
		);
	}

	@Test
	public void getMedicationsOnlyWithoutMedicalRecordLink_test () {
		List<Medication> m = medicationService.getMedicationsOnlyWithoutMedicalRecordLink();

		assertEquals(medications, m);
		assertEquals(medications.get(0), m.get(0));

		verify(medicationRepository, times(1)).findMedicationsOnlyWithNoLinks();
	}

	@Test
	public void deleteMedication_existingMedication_test () {
		medicationService.deleteMedication(existingMedication);

		verify(medicationRepository, times(1)).existsById(existingMedication.getId());
		verify(medicationRepository, times(1)).delete(existingMedication);
	}

	@Test
	public void deleteMedication_nonExistentMedication_test () {
		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> medicationService.deleteMedication(nonExistentMedication)
		);

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

		verify(medicationRepository, times(1)).existsById(nonExistentMedication.getId());
		verify(medicationRepository, never()).delete(nonExistentMedication);
	}

	@Test
	public void deleteAllergiesWithoutMedicalRecordLink_test() {
		medicationService.deleteMedicationsWithoutMedicalRecordLink();

		verify(medicationRepository, times(1)).findMedicationsOnlyWithNoLinks();
		verify(medicationRepository, times(1)).existsById(existingMedication.getId());
	}

}
