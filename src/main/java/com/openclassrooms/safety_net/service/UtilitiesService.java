package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.model.FireStation;
import com.openclassrooms.safety_net.model.MedicalRecord;
import com.openclassrooms.safety_net.model.Person;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.response.AddressInfo;
import com.openclassrooms.safety_net.model.response.PersonInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
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

	public PersonInfo getPersonInfo (String firstName, String lastName) throws ResponseStatusException {
		PersonId id = new PersonId(firstName, lastName);

		Person person = personService.getPersonById(id);
		MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(id);

		Period periodBirthday = Period.between(medicalRecord.getBirthday(), LocalDate.now());
		int age = periodBirthday.getYears();

		return new PersonInfo(firstName, lastName, person.getEmail(), age, person.getAddress(), medicalRecord.getAllergies(), medicalRecord.getMedications());
	}

	public List<AddressInfo> getPersonsCoveredByFireStationsInfos (int stationNumber) {
		Iterable<FireStation> fireStations = fireStationService.getFireStationsByStationNumber(stationNumber);

		List<Address> addresses = StreamSupport.stream(fireStations.spliterator(), false).map(FireStation::getAddress).toList();

		List<AddressInfo> addressesInfos = addresses.stream().map(address -> {
			List<Person> residents = address.getResidents();

			List<PersonInfo> residentsInfo = residents.stream().map(person -> {
				MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(new PersonId(person.getFirstName(), person.getLastName()));

				Period period = Period.between(medicalRecord.getBirthday(), LocalDate.now());
				int age = period.getYears();

				return new PersonInfo(person.getFirstName(), person.getLastName(), person.getEmail(), age, person.getAddress(), medicalRecord.getAllergies(), medicalRecord.getMedications());
			}).toList();

			return new AddressInfo(address.getLabel(), address.getZip(), address.getCity(), residentsInfo);
		}).toList();

		return addressesInfos;
	}

}
