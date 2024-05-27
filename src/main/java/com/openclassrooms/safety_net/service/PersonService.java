package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.model.Person;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.update.PersonUpdate;
import com.openclassrooms.safety_net.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PersonService {
	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private AddressService addressService;


	public Person getPersonById (PersonId personId) throws ResponseStatusException {
		Optional<Person> person = personRepository.findById(personId);
		if (person.isPresent()) {
			return person.get();
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person who called %s %s not found.".formatted(personId.getFirstName(), personId.getLastName()));
		}
	}

	public Iterable<String> getPersonsEmailByCity (String city) throws ResponseStatusException {
		Iterable<String> personsEmail = personRepository.findPersonsEmailByCity(city);
		if (personsEmail.iterator().hasNext()) {
			return personsEmail;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No person found.");
		}
	}

	public Iterable<Person> getPersons () throws ResponseStatusException {
		Iterable<Person> persons = personRepository.findAll();
		if (persons.iterator().hasNext()) {
			return persons;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No person found.");
		}
	}


	public Person updatePersonProperties (Person person, String email, String phone, Address address) {
		if (!person.getEmail().equalsIgnoreCase(email))
			person.setEmail(email);
		if (!person.getPhone().equalsIgnoreCase(phone))
			person.setPhone(phone);

		if (addressService.addressModified(person.getAddress(), address)) {
			person.setAddress(address);
		}

		return person;
	}

	public Person updatePerson (PersonId id, PersonUpdate personUpdate) throws ResponseStatusException {
		Person person = getPersonById(id);

		Address actualAddress = person.getAddress();
		Address newAddress = personUpdate.getAddress();

		if (addressService.addressModified(actualAddress, newAddress)) {
			int countResidentsAndFireStations = addressService.countResidentsAndFireStations(actualAddress.getLabel(), actualAddress.getZip(), actualAddress.getCity());
			if (countResidentsAndFireStations <= 1) {
				newAddress = addressService.updateAddress(actualAddress, personUpdate.getAddress());
			} else {
				Address existingAddress = addressService.getAddressByLabelAndZipAndCity(newAddress.getLabel(), newAddress.getZip(), newAddress.getCity());
				if (existingAddress != null) {
					newAddress = existingAddress;
				}
			}
		}

		Person updatedPerson = updatePersonProperties(person, personUpdate.getEmail(), personUpdate.getPhone(), newAddress);

		return personRepository.save(updatedPerson);
	}

	public Person addPerson (Person person) throws ResponseStatusException {
		try {
			Address newPersonAddress = person.getAddress();

			Address address = addressService.getAddressByLabelAndZipAndCity(newPersonAddress.getLabel(), newPersonAddress.getZip(), newPersonAddress.getCity());

			if (address != null) {
				person.setAddress(address);
			}

			return personRepository.save(person);
		} catch (DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Person who called %s %s already exist.".formatted(person.getFirstName(), person.getLastName()));
		}
	}

	public void deletePersonById (PersonId personId) throws ResponseStatusException {
		if (personRepository.existsById(personId)) {
			personRepository.deleteById(personId);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person who called %s %s not found and can't be deleted.".formatted(personId.getFirstName(), personId.getLastName()));
		}
	}

}
