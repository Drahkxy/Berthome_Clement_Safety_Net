package com.openclassrooms.safety_net.controller;

import com.openclassrooms.safety_net.model.Person;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.update.PersonUpdate;
import com.openclassrooms.safety_net.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class PersonController {
	@Autowired
	private PersonService personService;


	@GetMapping("/persons")
	public ResponseEntity<Iterable<Person>> getPersons () {
		try {
			Iterable<Person> persons = personService.getPersons();
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(persons);
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

	@GetMapping("/person")
	public ResponseEntity<Person> getPersonById (@RequestParam("firstname") final String firstname, @RequestParam("lastname") final String lastname) {
		try {
			Person person = personService.getPersonById((new PersonId(firstname, lastname)));
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(person);
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

	@DeleteMapping("/person")
	public ResponseEntity deletePerson (@RequestParam("firstname") final String firstname, @RequestParam("lastname") final String lastname) {
		try {
			personService.deletePersonById(new PersonId(firstname, lastname));
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


	@PostMapping("/person")
	@ResponseBody
	public ResponseEntity<Person> addPerson (@RequestBody Person person) {
		try {
			Person createdPerson = personService.addPerson(person);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(createdPerson);
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

	@PatchMapping("/person")
	public ResponseEntity<Person> updatePerson (@RequestParam("firstname") final String firstname, @RequestParam("lastname") final String lastname, @RequestBody PersonUpdate personUpdate) {
		try {
			Person personUpdated = personService.updatePerson(new PersonId(firstname, lastname), personUpdate);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(personUpdated);
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
