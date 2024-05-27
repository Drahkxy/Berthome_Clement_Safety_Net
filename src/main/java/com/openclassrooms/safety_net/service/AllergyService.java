package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Allergy;
import com.openclassrooms.safety_net.repository.AllergyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class AllergyService {
	@Autowired
	private AllergyRepository allergyRepository;

	public Allergy getAllergyByName (String name) {
		Optional<Allergy> allergy = allergyRepository.findByName(name);
		return allergy.orElse(null);
	}

	public List<Allergy> getAllergiesOnlyWithNoLinks () {
		Iterable<Allergy> allergies = allergyRepository.findAllergiesOnlyWithNoLinks();
		return StreamSupport.stream(allergies.spliterator(), false).toList();
	}

	public void deleteAllergy (Allergy allergy) throws ResponseStatusException {
		if (allergyRepository.existsById(allergy.getId())) {
			allergyRepository.delete(allergy);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Allergy %s not found and can't be deleted.".formatted(allergy.getName()));
		}
	}

	public void deleteAllergiesWithoutMedicalRecordLink () {
		Iterable<Allergy> allergies = getAllergiesOnlyWithNoLinks();
		try {
			allergies.forEach(this::deleteAllergy);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
		}
	}
}
