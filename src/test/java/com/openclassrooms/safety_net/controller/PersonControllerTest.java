package com.openclassrooms.safety_net.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.model.Person;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.update.PersonUpdate;
import com.openclassrooms.safety_net.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PersonController.class)
public class PersonControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private PersonService personService;

	private String personJson;
	private Person person;
	private Person otherPerson;
	private PersonId existingPersonId;
	private PersonId nonExistentPersonId;

	@BeforeEach
	public void setUp () throws JsonProcessingException {
		Address address = new Address("123 Main St", "12345", "Springfield");
		address.setId(1);

		Person p = new Person("John", "Doe", "0123456789", "john.doe@exemple.com");
		Person otherP = new Person("Jane", "Doe", "9876543210", "doe.jane@exemple.fr");
		address.addResident(p);
		address.addResident(otherP);

		personJson = objectMapper.writeValueAsString(p);
		person = objectMapper.readValue(personJson, Person.class);

		String otherPersonJson = objectMapper.writeValueAsString(otherP);
		otherPerson = objectMapper.readValue(otherPersonJson, Person.class);

		existingPersonId = new PersonId(person.getFirstName(), person.getLastName());
		nonExistentPersonId = new PersonId("non", "existent");
	}

	@Test
	public void getPersons_success_test () throws Exception {
		List<Person> persons = List.of(person, otherPerson);

		when(personService.getPersons()).thenReturn(persons);

		mockMvc.perform(get("/persons"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].firstName").value(person.getFirstName()))
				.andExpect(jsonPath("$[0].lastName").value(person.getLastName()))
				.andExpect(jsonPath("$[0].phone").value(person.getPhone()))
				.andExpect(jsonPath("$[0].email").value(person.getEmail()))
				.andExpect(jsonPath("$[0].address.label").value(person.getAddress().getLabel()))
				.andExpect(jsonPath("$[0].address.zip").value(person.getAddress().getZip()))
				.andExpect(jsonPath("$[0].address.city").value(person.getAddress().getCity()))
				.andExpect(jsonPath("$[1].firstName").value(otherPerson.getFirstName()))
				.andExpect(jsonPath("$[1].lastName").value(otherPerson.getLastName()))
				.andExpect(jsonPath("$[1].phone").value(otherPerson.getPhone()))
				.andExpect(jsonPath("$[1].email").value(otherPerson.getEmail()))
				.andExpect(jsonPath("$[1].address.label").value(otherPerson.getAddress().getLabel()))
				.andExpect(jsonPath("$[1].address.zip").value(otherPerson.getAddress().getZip()))
				.andExpect(jsonPath("$[1].address.city").value(otherPerson.getAddress().getCity()));

		verify(personService, times(1)).getPersons();
	}

	@Test
	public void getPersons_throwsException_test () throws Exception {
		when(personService.getPersons()).thenThrow(new RuntimeException("Service exception"));

		mockMvc.perform(get("/persons"))
				.andExpect(status().isInternalServerError());

		verify(personService, times(1)).getPersons();
	}

	@Test
	public void getPersons_throwsResponseStatusException_test () throws Exception {
		when(personService.getPersons()).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No person found."));

		mockMvc.perform(get("/persons"))
				.andExpect(status().isNotFound());

		verify(personService, times(1)).getPersons();
	}

	@Test
	void getPersonById_success_test () throws Exception {
		when(personService.getPersonById(existingPersonId)).thenReturn(person);

		mockMvc.perform(get("/person?firstname=%s&lastname=%s".formatted(existingPersonId.getFirstName(), existingPersonId.getLastName())))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("firstName").value(person.getFirstName()))
				.andExpect(jsonPath("lastName").value(person.getLastName()))
				.andExpect(jsonPath("phone").value(person.getPhone()))
				.andExpect(jsonPath("email").value(person.getEmail()))
				.andExpect(jsonPath("address.label").value(person.getAddress().getLabel()))
				.andExpect(jsonPath("address.zip").value(person.getAddress().getZip()))
				.andExpect(jsonPath("address.city").value(person.getAddress().getCity()));

		verify(personService, times(1)).getPersonById(existingPersonId);
	}

	@Test
	void getPersonById_throwsException_test () throws Exception {
		when(personService.getPersonById(nonExistentPersonId)).thenThrow(new RuntimeException("Service Exception"));

		mockMvc.perform(get("/person?firstname=%s&lastname=%s".formatted(nonExistentPersonId.getFirstName(), nonExistentPersonId.getLastName())))
				.andExpect(status().isInternalServerError());

		verify(personService, times(1)).getPersonById(nonExistentPersonId);
	}

	@Test
	void getPersonById_throwsResponseStatusException_test () throws Exception {
		when(personService.getPersonById(nonExistentPersonId))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No person found."));

		mockMvc.perform(get("/person?firstname=%s&lastname=%s".formatted(nonExistentPersonId.getFirstName(), nonExistentPersonId.getLastName())))
				.andExpect(status().isNotFound());

		verify(personService, times(1)).getPersonById(nonExistentPersonId);
	}

	@Test
	public void deletePerson_success_test () throws Exception {
		doNothing().when(personService).deletePersonById(existingPersonId);

		mockMvc.perform(delete("/person?firstname=%s&lastname=%s".formatted(existingPersonId.getFirstName(), existingPersonId.getLastName())))
				.andExpect(status().isOk());

		verify(personService, times(1)).deletePersonById(existingPersonId);
	}

	@Test
	public void deletePerson_throwsException_test () throws Exception {
		doThrow(new RuntimeException("Service Exception")).when(personService).deletePersonById(nonExistentPersonId);

		mockMvc.perform(delete("/person?firstname=%s&lastname=%s".formatted(nonExistentPersonId.getFirstName(), nonExistentPersonId.getLastName())))
				.andExpect(status().isInternalServerError());

		verify(personService, times(1)).deletePersonById(nonExistentPersonId);
	}

	@Test
	public void deletePerson_throwsResponseStatusException_test () throws Exception {
		doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found and can't be deleted."))
				.when(personService).deletePersonById(nonExistentPersonId);

		mockMvc.perform(delete("/person?firstname=%s&lastname=%s".formatted(nonExistentPersonId.getFirstName(), nonExistentPersonId.getLastName())))
				.andExpect(status().isNotFound());

		verify(personService, times(1)).deletePersonById(nonExistentPersonId);
	}

	@Test
	public void addPerson_success_test () throws Exception {
		when(personService.addPerson(person)).thenReturn(person);

		mockMvc.perform(
				post("/person")
						.contentType(MediaType.APPLICATION_JSON)
						.content(personJson)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("firstName").value(person.getFirstName()))
				.andExpect(jsonPath("lastName").value(person.getLastName()))
				.andExpect(jsonPath("phone").value(person.getPhone()))
				.andExpect(jsonPath("email").value(person.getEmail()))
				.andExpect(jsonPath("address").value(person.getAddress()));

		verify(personService, times(1)).addPerson(person);
	}

	@Test
	public void addPerson_throwsException_test () throws Exception {
		when(personService.addPerson(person)).thenThrow(new RuntimeException("Service exception"));

		mockMvc.perform(
				post("/person")
						.contentType(MediaType.APPLICATION_JSON)
						.content(personJson)
				)
				.andExpect(status().isInternalServerError());

		verify(personService, times(1)).addPerson(person);
	}

	@Test
	public void addPerson_throwsResponseStatusException_test () throws Exception {
		when(personService.addPerson(person))
				.thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "This person already exist"));

		mockMvc.perform(
				post("/person")
						.contentType(MediaType.APPLICATION_JSON)
						.content(personJson)
				)
				.andExpect(status().isBadRequest());

		verify(personService, times(1)).addPerson(person);
	}

	@Test
	public void updatePerson_success_test () throws Exception {
		PersonUpdate personUpdate = new PersonUpdate();
		String personUpdateJson = objectMapper.writeValueAsString(personUpdate);
		personUpdate = objectMapper.readValue(personUpdateJson, PersonUpdate.class);

		when(personService.updatePerson(existingPersonId, personUpdate)).thenReturn(person);

		mockMvc.perform(
				patch("/person?firstname=%s&lastname=%s".formatted(existingPersonId.getFirstName(), existingPersonId.getLastName()))
						.contentType(MediaType.APPLICATION_JSON)
						.content(personUpdateJson))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("firstName").value(person.getFirstName()))
				.andExpect(jsonPath("lastName").value(person.getLastName()))
				.andExpect(jsonPath("phone").value(person.getPhone()))
				.andExpect(jsonPath("email").value(person.getEmail()))
				.andExpect(jsonPath("address").value(person.getAddress()));

		verify(personService, times(1)).updatePerson(existingPersonId, personUpdate);
	}

	@Test
	public void updatePerson_throwsException_test () throws Exception {
		PersonUpdate personUpdate = new PersonUpdate();

		when(personService.updatePerson(nonExistentPersonId, personUpdate))
				.thenThrow(new RuntimeException("Service exception"));

		mockMvc.perform(
				patch("/person?firstname=%s&lastname=%s".formatted(nonExistentPersonId.getFirstName(), nonExistentPersonId.getLastName()))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(personUpdate)))
				.andExpect(status().isInternalServerError());

		verify(personService, times(1)).updatePerson(nonExistentPersonId, personUpdate);
	}

	@Test
	public void updatePerson_throwsResponseStatusException_test () throws Exception {
		PersonUpdate personUpdate = new PersonUpdate();

		when(personService.updatePerson(nonExistentPersonId, personUpdate))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found and can't be updated."));

		mockMvc.perform(
						patch("/person?firstname=%s&lastname=%s".formatted(nonExistentPersonId.getFirstName(), nonExistentPersonId.getLastName()))
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(personUpdate)))
				.andExpect(status().isNotFound());

		verify(personService, times(1)).updatePerson(nonExistentPersonId, personUpdate);
	}

}
