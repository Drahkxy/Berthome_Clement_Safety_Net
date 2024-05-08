package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Person;
import com.openclassrooms.safety_net.repository.PersonRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Data
public class PersonService {
	@Autowired
	private PersonRepository personRepository;

	public Person addPerson (Person person) {
		return personRepository.save(person);
	}

	public Iterable<Person> addPersons (List<Person> persons) {
		return personRepository.saveAll(persons);
	}

	public Optional<Person> getPersonById(int id) {
		return personRepository.findById(id);
	}

	public Iterable<Person> getPersons() {
		return personRepository.findAll();
	}

	public Optional<Person> getPersonByName(String firstName, String lastName) {
		return personRepository.findByFirstNameAndLastName(firstName, lastName);
	}
}
