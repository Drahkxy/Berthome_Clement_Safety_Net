package com.openclassrooms.safety_net.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safety_net.model.*;
import com.openclassrooms.safety_net.model.dto.FireStationDTO;
import com.openclassrooms.safety_net.model.dto.GlobalDTO;
import com.openclassrooms.safety_net.model.dto.MedicalRecordDTO;
import com.openclassrooms.safety_net.model.dto.PersonDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class Mapper {
	@Autowired
	private FileJsonReader jsonDataReader;
	private String dataJson;

	private GlobalDTO globalDTO;
	private final List<Address> addresses = new ArrayList<>();
	private final List<Person> persons = new ArrayList<>();
	private final List<MedicalRecord> medicalRecords = new ArrayList<>();
	private final List<FireStation> fireStations = new ArrayList<>();


	public void startMapping (String resourcePath) {
		jsonDataReader.setFilePath(resourcePath);
		dataJson = jsonDataReader.getJsonData();
		getData();
		createAddresses();
		mapPersonsDTOtoPersonsEntity();
		mapFireStationsDTOtoFireStationsEntity();
		mapMedicalRecordsDTOtoMedicalRecordsEntity();
	}

	public List<Person> getPersons () {
		return persons;
	}

	public List<MedicalRecord> getMedicalRecords () {
		return medicalRecords;
	}

	public List<FireStation> getFireStations () {
		return fireStations;
	}


	public void getData () {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			globalDTO = objectMapper.readValue(dataJson, GlobalDTO.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public void mapMedicalRecordsDTOtoMedicalRecordsEntity () {
		List<MedicalRecordDTO> medicalRecordDTOList = globalDTO.getMedicalrecords();

		List<Allergy> alreadyExistentAllergies = new ArrayList<>();
		List<Medication> alreadyExistentMedications = new ArrayList<>();

		medicalRecordDTOList.forEach(medicalRecordDTO -> {
			String firstName = medicalRecordDTO.getFirstName();
			String lastName = medicalRecordDTO.getLastName();
			LocalDate birthday = mapStringBirthdayToLocalDateBirthday(medicalRecordDTO.getBirthdate());
			List<Allergy> allergies = manageAllergies(alreadyExistentAllergies, medicalRecordDTO.getAllergies());
			List<Medication> medications = manageMedications(alreadyExistentMedications, medicalRecordDTO.getMedications());

			MedicalRecord medicalRecord = new MedicalRecord(firstName, lastName, birthday);
			medicalRecord.setAllergies(allergies);
			medicalRecord.setMedications(medications);

			medicalRecords.add(medicalRecord);
		});
	}

	public void createAddresses () {
		List<PersonDTO> personsDTO = globalDTO.getPersons();

		personsDTO.forEach(personDTO -> {
			String label = personDTO.getAddress();
			String zip = personDTO.getZip();
			String city = personDTO.getCity();

			List<Address> addressesFiltered = addresses.stream()
					.filter(address -> address.getLabel().equalsIgnoreCase(label) && address.getZip().equalsIgnoreCase(zip) && address.getCity().equalsIgnoreCase(city))
					.toList();

			if (addressesFiltered.size() == 0) {
				addresses.add(new Address(label, zip, city));
			}
		});
	}

	public void mapPersonsDTOtoPersonsEntity () {
		List<PersonDTO> personsDTO = globalDTO.getPersons();

		personsDTO.forEach(personDTO -> {
			Address address = addresses.stream().filter(a -> {
				return a.getLabel().equalsIgnoreCase(personDTO.getAddress())
						|| a.getZip().equalsIgnoreCase(personDTO.getZip())
						|| a.getCity().equalsIgnoreCase(personDTO.getCity());
			}).toList().get(0);

			Person person = new Person(personDTO.getFirstName(), personDTO.getLastName(), personDTO.getPhone(), personDTO.getEmail());
			address.addResident(person);

			persons.add(person);
		});
	}

	public void mapFireStationsDTOtoFireStationsEntity () {
		List<FireStationDTO> fireStationsDTO = globalDTO.getFirestations();

		fireStationsDTO.forEach(fireStationDTO -> {
			Address address = addresses.stream().filter(a -> a.getLabel().equalsIgnoreCase(fireStationDTO.getAddress()))
					.toList()
					.get(0);

			FireStation fireStation = new FireStation(Integer.parseInt(fireStationDTO.getStation()));
			address.addFireStation(fireStation);

			fireStations.add(fireStation);
		});
	}

	public LocalDate mapStringBirthdayToLocalDateBirthday (String birthday) {
		String[] birthdaySplited = birthday.split("/");

		int day = Integer.parseInt(birthdaySplited[1]);
		int month = Integer.parseInt(birthdaySplited[0]);
		int year = Integer.parseInt(birthdaySplited[2]);

		return LocalDate.of(year, month, day);
	}

	public Medication mapStringMedicationToMedication (String stringMedication) {
		String[] medicationSplited = stringMedication.split(":");

		String name = medicationSplited[0];
		String stringDosage = medicationSplited[1];
		int dosage = Integer.parseInt(stringDosage.substring(0, stringDosage.length() - 2));

		return new Medication(name, dosage);
	}

	public List<Allergy> manageAllergies (List<Allergy> alreadyExistentAllergies, String[] stringAllergies) {
		List<Allergy> allergiesToReturn = new ArrayList<>();

		for (String s : stringAllergies) {
			List<Allergy> allergyFiltered = alreadyExistentAllergies.stream()
					.filter(a -> a.getName().equalsIgnoreCase(s))
					.toList();

			if (allergyFiltered.size() == 0) {
				Allergy a = new Allergy(s);
				allergiesToReturn.add(a);
				alreadyExistentAllergies.add(a);
			} else {
				allergiesToReturn.add(allergyFiltered.get(0));
			}
		}

		return allergiesToReturn;
	}

	public List<Medication> manageMedications (List<Medication> alreadyExistentMedications, String[] stringMedications) {
		List<Medication> medicationsToReturn = new ArrayList<>();

		for (String s : stringMedications) {
			Medication m = mapStringMedicationToMedication(s);

			List<Medication> medicationsFiltered = alreadyExistentMedications.stream()
					.filter(medication -> medication.getName().equalsIgnoreCase(m.getName()) && medication.getDosage() == m.getDosage())
					.toList();

			if (medicationsFiltered.size() == 0) {
				medicationsToReturn.add(m);
				alreadyExistentMedications.add(m);
			} else {
				medicationsToReturn.add(medicationsFiltered.get(0));
			}
		}

		return medicationsToReturn;
	}

}
