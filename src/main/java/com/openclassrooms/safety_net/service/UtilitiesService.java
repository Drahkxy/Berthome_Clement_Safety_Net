package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.model.FireStation;
import com.openclassrooms.safety_net.model.MedicalRecord;
import com.openclassrooms.safety_net.model.Person;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.response.AddressInfo;
import com.openclassrooms.safety_net.model.response.ChildInfo;
import com.openclassrooms.safety_net.model.response.FireInfo;
import com.openclassrooms.safety_net.model.response.PersonInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class UtilitiesService {
	@Autowired
	private PersonService personService;

	@Autowired
	private MedicalRecordService medicalRecordService;

	@Autowired
	private FireStationService fireStationService;

	@Autowired
	private AddressService addressService;

	public PersonInfo getPersonInfo (String firstName, String lastName) throws ResponseStatusException {
		PersonId id = new PersonId(firstName, lastName);

		Person person = personService.getPersonById(id);
		MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(id);

		return new PersonInfo(firstName, lastName, person.getEmail(), medicalRecord.getAge(), person.getAddress(), medicalRecord.getAllergies(), medicalRecord.getMedications());
	}

	public List<AddressInfo> getPersonsCoveredByFireStationsInfos (int stationNumber) throws ResponseStatusException {
		Iterable<FireStation> fireStations = fireStationService.getFireStationsByStationNumber(stationNumber);

		List<Address> addresses = StreamSupport.stream(fireStations.spliterator(), false).map(FireStation::getAddress).toList();

		List<AddressInfo> addressesInfos = addresses.stream().map(address -> {
			List<Person> residents = address.getResidents();

			List<PersonInfo> residentsInfo = residents.stream().map(person -> {
				MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(new PersonId(person.getFirstName(), person.getLastName()));

				return new PersonInfo(person.getFirstName(), person.getLastName(), person.getEmail(), medicalRecord.getAge(), person.getAddress(), medicalRecord.getAllergies(), medicalRecord.getMedications());
			}).toList();

			return new AddressInfo(address.getLabel(), address.getZip(), address.getCity(), residentsInfo);
		}).toList();

		return addressesInfos;
	}

	public FireInfo getFireInfos (String label, String zip, String city) throws ResponseStatusException {
		Address address = addressService.getAddressByLabelAndZipAndCity(label, zip, city);
		if (address == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "%s %s %s address not found.".formatted(label, zip, city));
		}

		List<Integer> fireStationsNumber = address.getFireStations().stream().map(FireStation::getStation).toList();

		var personsInfos = address.getResidents().stream().map(person -> {
			String firstName = person.getFirstName();
			String lastName = person.getLastName();
			PersonId id = new PersonId(firstName, lastName);

			MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(id);

			return new PersonInfo(firstName, lastName, person.getEmail(), medicalRecord.getAge(), person.getAddress(), medicalRecord.getAllergies(), medicalRecord.getMedications());
		}).toList();

		return new FireInfo(fireStationsNumber, personsInfos);
	}

	public List<String> getPhoneAlertInfos (int fireStationId) throws ResponseStatusException {
		FireStation fireStation = fireStationService.getFireStationById(fireStationId);
		Address fireStationAddress = fireStation.getAddress();
		List<Person> personsCoveredByFireStation = fireStationAddress.getResidents();
		return personsCoveredByFireStation.stream().map(Person::getPhone).toList();
	}

	public List<ChildInfo> getChildAlertInfos (String label, String zip, String city) throws ResponseStatusException {
		Address address = addressService.getAddressByLabelAndZipAndCity(label, zip, city);
		if (address == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "%s %s %s address not found.".formatted(label, zip, city));
		}

		List<Person> residents = address.getResidents();
		List<ChildInfo> children = new ArrayList<>();

		List<PersonInfo> residentsInformations = residents.stream().map(person -> {
			String firstName = person.getFirstName();
			String lastName = person.getLastName();
			PersonId id = new PersonId(firstName, lastName);

			MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(id);

			return new PersonInfo(firstName, lastName, person.getEmail(), medicalRecord.getAge(), person.getAddress(), medicalRecord.getAllergies(), medicalRecord.getMedications());
		}).sorted(Comparator.comparingInt(PersonInfo::getAge)).toList();

		residentsInformations.forEach(person -> {
			int age = person.getAge();

			String firstName = person.getFirstName();
			String lastName = person.getLastName();

			if (age < 18) {
				ChildInfo childInfo = new ChildInfo(firstName, lastName, age);
				children.add(childInfo);
			} else {
				children.forEach(child -> {
					if (child.getLastName().equalsIgnoreCase(lastName)) {
						child.addFamilyMember(firstName + " " + lastName);
					}
				});
			}
		});

		return children;
	}

}
