package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Allergy;
import com.openclassrooms.safety_net.repository.AllergyRepository;
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
public class AllergyServiceTest {
	@Mock
	private AllergyRepository allergyRepository;

	@InjectMocks
	private AllergyService allergyService;

	private Allergy allergy;

	@BeforeEach
	public void setUp () {
		allergy = new Allergy("peanut");
	}

	@Test
	public void getAllergyByName_existingAllergy_test () {
		String name = "peanut";

		when(allergyRepository.findByName(name)).thenReturn(Optional.of(allergy));

		Allergy a = allergyService.getAllergyByName(name);

		assertEquals(allergy, a);

		verify(allergyRepository, times(1)).findByName(name);
	}

	@Test
	public void getAllergyByName_nonExistentAllergy_test () {
		String name = "pineapple";

		when(allergyRepository.findByName(name)).thenReturn(Optional.empty());

		Allergy a = allergyService.getAllergyByName(name);

		assertNull(a);

		verify(allergyRepository, times(1)).findByName(name);
	}

	@Test
	public void getAllergiesOnlyWithNoLinks_test () {
		List<Allergy> allergies = List.of(allergy);

		when(allergyRepository.findAllergiesOnlyWithNoLinks()).thenReturn(allergies);

		List<Allergy> a = allergyService.getAllergiesOnlyWithNoLinks();

		assertEquals(allergies, a);

		verify(allergyRepository, times(1)).findAllergiesOnlyWithNoLinks();
	}

	@Test
	public void deleteAllergy_existingAllergy_test () {
		when(allergyRepository.existsById(allergy.getId())).thenReturn(true);

		allergyService.deleteAllergy(allergy);

		verify(allergyRepository, times(1)).existsById(allergy.getId());
		verify(allergyRepository, times(1)).delete(allergy);
	}

	@Test
	public void deleteAllergy_nonExistentAllergy_test () {
		Allergy fakeAllergy = new Allergy();

		when(allergyRepository.existsById(anyInt())).thenReturn(false);

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> allergyService.deleteAllergy(fakeAllergy)
		);

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

		verify(allergyRepository, times(1)).existsById(anyInt());
		verify(allergyRepository, never()).delete(fakeAllergy);
	}

	@Test
	public void deleteAllergiesWithoutMedicalRecordLink_test() {
		List<Allergy> allergies = List.of(allergy);

		when(allergyRepository.findAllergiesOnlyWithNoLinks()).thenReturn(allergies);
		when(allergyRepository.existsById(allergy.getId())).thenReturn(true);

		allergyService.deleteAllergiesWithoutMedicalRecordLink();

		verify(allergyRepository, times(1)).findAllergiesOnlyWithNoLinks();
		verify(allergyRepository, times(allergies.size())).delete(allergy);
	}

}
