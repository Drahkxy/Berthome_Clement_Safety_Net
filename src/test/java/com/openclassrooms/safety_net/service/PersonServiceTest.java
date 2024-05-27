package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.model.Person;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.update.PersonUpdate;
import com.openclassrooms.safety_net.repository.PersonRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PersonServiceTest {
	@Mock
	private PersonRepository personRepository;

	@Mock
	private AddressService addressService;

	@InjectMocks
	private PersonService personService;

	private static PersonId existingPersonId;
	private static PersonId nonExistentPersonId;
	private static Person person;

	@BeforeAll
	public static void setUp() {
		existingPersonId = new PersonId("John", "Doe");
		nonExistentPersonId = new PersonId("Jane", "Doe");
	}

	@BeforeEach
	public void eachSetUp () {
		String phone = "1234567890";
		String email = "john.doe@example.com";
		Address address = new Address("123 Main St", "12345", "Springfield");

		person = new Person(existingPersonId.getFirstName(), existingPersonId.getLastName(), phone, email);
		person.setAddress(address);
	}

	@Test
	public void getPersonById_personExist_test () {
		when(personRepository.findById(existingPersonId)).thenReturn(Optional.of(person));

		Person p = personService.getPersonById(existingPersonId);

		assertEquals(person, p, "Returned person should be %s %s.".formatted(person.getFirstName(), person.getLastName()));

		verify(personRepository, times(1)).findById(existingPersonId);
	}

	@Test
	public void getPersonById_personDoesNotExist_test () {
		when(personRepository.findById(nonExistentPersonId)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			personService.getPersonById(nonExistentPersonId);
		});

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "Http error code should be 404 NOT_FOUND.");

		verify(personRepository, times(1)).findById(nonExistentPersonId);
	}

	@Test
	public void getPersons_completedList_test () {
		when(personRepository.findAll()).thenReturn(List.of(person));

		Iterable<Person> persons = personService.getPersons();

		assertNotNull(persons, "Returned list of persons should not be null");

		List<Person> personsList = StreamSupport.stream(persons.spliterator(), false).toList();

		assertEquals(1, personsList.size(), "Size of returned list should be 1.");
		assertEquals(person, personsList.get(0), "First person in the returned list should be %s %s".formatted(person.getFirstName(), person.getLastName()));

		verify(personRepository, times(1)).findAll();
	}

	@Test
	public void getPersons_emptyList_test () {
		when(personRepository.findAll()).thenReturn(List.of());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			personService.getPersons();
		});

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "Http error code should be 404 NOT_FOUND.");

		verify(personRepository, times(1)).findAll();
	}

	@Test
	public void getPersonsEmailByCity_completedList_test () {
		String city = person.getAddress().getCity();
		String email = person.getEmail();

		when(personRepository.findPersonsEmailByCity(city)).thenReturn(List.of(email));

		Iterable<String> emails = personService.getPersonsEmailByCity(city);

		assertNotNull(emails, "Returned list of emails should not be null.");

		List<String> emailsList = StreamSupport.stream(emails.spliterator(), false).toList();

		assertEquals(1, emailsList.size(), "Size of returned list should be 1");
		assertEquals(email, emailsList.get(0), "First email in the returned list should be %s.".formatted(email));

		verify(personRepository, times(1)).findPersonsEmailByCity(city);
	}

	@Test
	public void getPersonsEmailByCity_emptyList_test () {
		String city = "fakeCityName";

		when(personRepository.findPersonsEmailByCity(city)).thenReturn(List.of());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			personService.getPersonsEmailByCity(city);
		});

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "Http error code should be 404 NOT_FOUND");

		verify(personRepository, times(1)).findPersonsEmailByCity(city);
	}

	@Test
	public void deletePersonById_personExist_test () {
		when(personRepository.existsById(existingPersonId)).thenReturn(true);

		personService.deletePersonById(existingPersonId);

		verify(personRepository, times(1)).existsById(existingPersonId);
		verify(personRepository, times(1)).deleteById(existingPersonId);
	}

	@Test
	public void deletePersonById_personDoesNotExist_test () {
		when(personRepository.existsById(nonExistentPersonId)).thenReturn(false);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			personService.deletePersonById(nonExistentPersonId);
		});

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "Http error code should be 404 NOT_FOUND");

		verify(personRepository, times(1)).existsById(nonExistentPersonId);
		verify(personRepository, never()).deleteById(nonExistentPersonId);
	}

	@Test
	public void addPerson_successfullyPersonCreation_Test () {
		when(addressService.getAddressByLabelAndZipAndCity(anyString(), anyString(), anyString())).thenReturn(null);
		when(personRepository.save(person)).thenReturn(person);

		Person p = personService.addPerson(person);

		assertEquals(person, p, "Person returned by person creation methode should be %s %s.".formatted(person.getFirstName(), person.getLastName()));

		verify(addressService, times(1)).getAddressByLabelAndZipAndCity(anyString(), anyString(), anyString());
		verify(personRepository, times(1)).save(person);
	}

	@Test
	public void addPerson_personAlreadyExist_Test () {
		when(addressService.getAddressByLabelAndZipAndCity(anyString(), anyString(), anyString())).thenReturn(null);
		when(personRepository.save(person)).thenThrow(DataIntegrityViolationException.class);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			personService.addPerson(person);
		});

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode(), "Http error code should be 400 BAD_REQUEST.");

		verify(addressService, times(1)).getAddressByLabelAndZipAndCity(anyString(), anyString(), anyString());
		verify(personRepository, times(1)).save(person);
	}

	@Test
	public void updatePersonProperties_withModifiedProperties_test () {
		Address newAddress = new Address("459 Elm St", "67890", "Shelbyville");
		String newPhone = "0987654321";
		String newEmail = "doe.john@example.com";

		when(addressService.addressModified(any(Address.class), any(Address.class))).thenReturn(true);

		Person personUpdated = personService.updatePersonProperties(person, newEmail, newPhone, newAddress);

		assertEquals(
				newPhone,
				personUpdated.getPhone(),
				"Phone number should be %s but is equal to %s.".formatted(newPhone, personUpdated.getPhone())
		);
		assertEquals(
				newEmail,
				personUpdated.getEmail(),
				"Email should be %s but is equal to %s.".formatted(newEmail, personUpdated.getEmail())
		);
		assertEquals(
				newAddress,
				personUpdated.getAddress(),
				"Address should be %s %s %s.".formatted(newAddress.getLabel(), newAddress.getZip(), newAddress.getCity())
		);

		verify(addressService, times(1)).addressModified(any(Address.class), any(Address.class));
	}

	@Test
	public void updatePerson_personDoesNotExist_test () {
		when(personRepository.findById(nonExistentPersonId)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			personService.updatePerson(nonExistentPersonId, any(PersonUpdate.class));
		});

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(), "Http error code should be 404 NOT_FOUND");

		verify(personRepository, times(1)).findById(nonExistentPersonId);
		verify(personRepository, never()).save(any(Person.class));
	}

	@Test
	public void updatePerson_personExistAndSameAddress_test () {
		when(personRepository.findById(existingPersonId)).thenReturn(Optional.of(person));
		when(personRepository.save(person)).thenReturn(person);

		PersonUpdate personUpdate = new PersonUpdate(person.getPhone(), person.getEmail(), person.getAddress());

		Person p = personService.updatePerson(existingPersonId, personUpdate);

		assertEquals(person, p);

		verify(personRepository, times(1)).findById(existingPersonId);
		verify(personRepository, times(1)).save(person);

		verify(addressService, never()).countResidentsAndFireStations(anyString(), anyString(), anyString());
		verify(addressService, never()).updateAddress(any(Address.class), any(Address.class));
		verify(addressService, never()).getAddressByLabelAndZipAndCity(anyString(), anyString(), anyString());
	}

	@Test
	public void updatePerson_personExistAndModifiedAddress_test () {
		Address actualAddress = person.getAddress();
		Address newAddress = new Address("459 Elm St", "67890", "Shelbyville");

		PersonUpdate personUpdate = new PersonUpdate(person.getPhone(), person.getEmail(), newAddress);

		when(personRepository.findById(existingPersonId)).thenReturn(Optional.of(person));
		when(addressService.addressModified(actualAddress, newAddress)).thenReturn(true);
		when(addressService.countResidentsAndFireStations(actualAddress.getLabel(), actualAddress.getZip(), actualAddress.getCity())).thenReturn(1);
		when(addressService.updateAddress(actualAddress, newAddress)).thenReturn(newAddress);
		when(personRepository.save(person)).thenReturn(person);

		Person p = personService.updatePerson(existingPersonId, personUpdate);

		assertEquals(person, p);

		verify(personRepository, times(1)).findById(existingPersonId);
		verify(addressService, times(2)).addressModified(actualAddress, newAddress);
		verify(addressService, times(1)).countResidentsAndFireStations(actualAddress.getLabel(), actualAddress.getZip(), actualAddress.getCity());
		verify(addressService, times(1)).updateAddress(actualAddress, newAddress);
		verify(addressService, never()).getAddressByLabelAndZipAndCity(anyString(), anyString(), anyString());
		verify(personRepository, times(1)).save(person);
	}

}
