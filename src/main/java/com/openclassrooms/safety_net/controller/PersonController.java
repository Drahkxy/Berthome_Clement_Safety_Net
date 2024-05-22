package com.openclassrooms.safety_net.controller;

import com.openclassrooms.safety_net.model.Person;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.update.PersonUpdate;
import com.openclassrooms.safety_net.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class PersonController {
	@Autowired
	private PersonService personService;


	@PostMapping("/person")
	public Person addPerson (@RequestBody Person person) {
		try {
			return personService.addPerson(person);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while creating person.");
		}
	}

	@PatchMapping("/person/{firstname}&{lastname}")
	public Person updatePerson (@PathVariable("firstname") final String firstname, @PathVariable("lastname") final String lastname, @RequestBody PersonUpdate personUpdate) {
		try {
			return personService.updatePerson(new PersonId(firstname, lastname), personUpdate);
		} catch (ResponseStatusException e) {
			System.out.println(e.getMessage());
			throw e;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while updating person");
		}
	}

	@DeleteMapping("/person/{firstname}&{lastname}")
	public void deletePerson (@PathVariable("firstname") final String firstname, @PathVariable("lastname") final String lastname) {
		try {
			personService.deletePersonById(new PersonId(firstname, lastname));
		} catch (ResponseStatusException e) {
			System.out.println(e.getMessage());
			throw e;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while deleting person.");
		}
	}

	@GetMapping("/person/{firstname}&{lastname}")
	public Person getPersonById (@PathVariable("firstname") final String firstname, @PathVariable("lastname") final String lastname) {
		try {
			return personService.getPersonById(new PersonId(firstname, lastname));
		} catch (ResponseStatusException e) {
			System.out.println(e.getMessage());
			throw e;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while retrieving person.");
		}
	}

	@GetMapping("/persons")
	public Iterable<Person> getPersons () {
		try {
			return personService.getPersons();
		} catch (ResponseStatusException e) {
			System.out.println(e.getMessage());
			throw e;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while retrieving persons.");
		}
	}

}
