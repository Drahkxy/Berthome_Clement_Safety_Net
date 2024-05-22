package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Allergy;
import com.openclassrooms.safety_net.repository.AllergyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public void deleteAllergy (Allergy allergy) {
		allergyRepository.delete(allergy);
	}

	public void deleteAllergiesWithoutMedicalRecordLink () {
		Iterable<Allergy> allergies = getAllergiesOnlyWithNoLinks();
		allergies.forEach(this::deleteAllergy);
	}
}
