package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.MedicalRecord;
import com.openclassrooms.safety_net.model.Person;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.response.PersonInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;

@Service
public class UtilitiesService {
	@Autowired
	private PersonService personService;

	@Autowired
	private MedicalRecordService medicalRecordService;

	public PersonInfo getPersonInfo (String firstName, String lastName) throws ResponseStatusException {
		PersonId id = new PersonId(firstName, lastName);

		Person person = personService.getPersonById(id);
		MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(id);

		Period periodBirthday = Period.between(medicalRecord.getBirthday(), LocalDate.now());
		int age = periodBirthday.getYears();

		return new PersonInfo(firstName, lastName, person.getEmail(), age, person.getAddress(), medicalRecord.getAllergies(), medicalRecord.getMedications());
	}

}
