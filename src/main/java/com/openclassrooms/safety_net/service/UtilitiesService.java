package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.model.FireStation;
import com.openclassrooms.safety_net.model.MedicalRecord;
import com.openclassrooms.safety_net.model.Person;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
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

	public PersonInfosNameEmailAgeAddressMedicals getPersonInfo (String firstName, String lastName) throws ResponseStatusException {
		PersonId id = new PersonId(firstName, lastName);

		Person person = personService.getPersonById(id);
		MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(id);

		return new PersonInfosNameEmailAgeAddressMedicals(firstName, lastName, person.getEmail(), medicalRecord.getAge(), person.getAddress(), medicalRecord.getAllergies(), medicalRecord.getMedications());
	}

	public List<AddressInfos> getHomesResidentsInformationsCoveredByFireStations (int stationNumber) throws ResponseStatusException {
		Iterable<FireStation> fireStations = fireStationService.getFireStationsByStationNumber(stationNumber);

		List<Address> addresses = StreamSupport.stream(fireStations.spliterator(), false).map(FireStation::getAddress).toList();

		List<AddressInfos> addressesInfos = addresses.stream().map(address -> {
			List<Person> residents = address.getResidents();

			List<PersonInfosNameEmailAgeAddressMedicals> residentsInfo = residents.stream().map(person -> {
				MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(new PersonId(person.getFirstName(), person.getLastName()));

				return new PersonInfosNameEmailAgeAddressMedicals(person.getFirstName(), person.getLastName(), person.getEmail(), medicalRecord.getAge(), person.getAddress(), medicalRecord.getAllergies(), medicalRecord.getMedications());
			}).toList();

			return new AddressInfos(address.getLabel(), address.getZip(), address.getCity(), residentsInfo);
		}).toList();

		return addressesInfos;
	}

	public FireInfos getFireInfos (String label, String zip, String city) throws ResponseStatusException {
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

			return new PersonInfosNameEmailAgeAddressMedicals(firstName, lastName, person.getEmail(), medicalRecord.getAge(), person.getAddress(), medicalRecord.getAllergies(), medicalRecord.getMedications());
		}).toList();

		return new FireInfos(fireStationsNumber, personsInfos);
	}

	public List<String> getPhoneAlertInfos (int fireStationId) throws ResponseStatusException {
		FireStation fireStation = fireStationService.getFireStationById(fireStationId);
		Address fireStationAddress = fireStation.getAddress();
		List<Person> personsCoveredByFireStation = fireStationAddress.getResidents();
		return personsCoveredByFireStation.stream().map(Person::getPhone).toList();
	}

	public List<ChildInfos> getChildAlertInfos (String label, String zip, String city) throws ResponseStatusException {
		Address address = addressService.getAddressByLabelAndZipAndCity(label, zip, city);
		if (address == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "%s %s %s address not found.".formatted(label, zip, city));
		}

		List<Person> residents = address.getResidents();
		List<ChildInfos> children = new ArrayList<>();

		List<PersonInfosNameEmailAgeAddressMedicals> residentsInformations = residents.stream().map(person -> {
			String firstName = person.getFirstName();
			String lastName = person.getLastName();
			PersonId id = new PersonId(firstName, lastName);

			MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(id);

			return new PersonInfosNameEmailAgeAddressMedicals(firstName, lastName, person.getEmail(), medicalRecord.getAge(), person.getAddress(), medicalRecord.getAllergies(), medicalRecord.getMedications());
		}).sorted(Comparator.comparingInt(PersonInfosNameEmailAgeAddressMedicals::getAge)).toList();

		residentsInformations.forEach(person -> {
			int age = person.getAge();

			String firstName = person.getFirstName();
			String lastName = person.getLastName();

			if (age < 18) {
				ChildInfos childInfos = new ChildInfos(firstName, lastName, age);
				children.add(childInfos);
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

	public PersonsCoveredByFireStationsInfos getPersonsCoveredByFireStationsInfos (int stationNumber) throws ResponseStatusException {
		Iterable<FireStation> fireStations = fireStationService.getFireStationsByStationNumber(stationNumber);

		Stream<Address> addresses = StreamSupport.stream(fireStations.spliterator(), false)
				.map(FireStation::getAddress);

		Stream<Person> addressesResidents = addresses.map(Address::getResidents)
				.flatMap(Collection::stream);

		List<PersonInfosNamePhoneAgeAddress> addressesResidentsInfos = addressesResidents.map(person -> {
					PersonId id = new PersonId(person.getFirstName(), person.getLastName());

					MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(id);

					return new PersonInfosNamePhoneAgeAddress(person.getFirstName(), person.getLastName(), person.getPhone(), medicalRecord.getAge(), person.getAddress());
				}).toList();

		long childrenCount = addressesResidentsInfos.stream()
				.filter(personInfos -> personInfos.getAge() <= 18)
				.count();

		long adultCount = addressesResidentsInfos.size() - childrenCount;

		List<PersonInfosNamePhoneAddress> residentsInfos = addressesResidentsInfos.stream()
				.map(personInfos -> new PersonInfosNamePhoneAddress(personInfos.getFirstName(), personInfos.getLastName(), personInfos.getPhone(), personInfos.getAddress()))
				.toList();

		return new PersonsCoveredByFireStationsInfos((int) childrenCount, (int) adultCount, residentsInfos);
	}

}
