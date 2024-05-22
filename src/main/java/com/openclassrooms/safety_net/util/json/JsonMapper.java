package com.openclassrooms.safety_net.util.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safety_net.model.*;
import com.openclassrooms.safety_net.util.json.model.FireStationJson;
import com.openclassrooms.safety_net.util.json.model.MedicalRecordJson;
import com.openclassrooms.safety_net.util.json.model.PersonJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonMapper {
	@Autowired
	private JsonReader jsonReader;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private List<PersonJson> mapPersonsStringToPersonJsonList (String persons) throws JsonProcessingException {
		return objectMapper.readValue(persons, new TypeReference<List<PersonJson>>() {});
	}

	private List<MedicalRecordJson> mapMedicalRecordsStringToMedicalRecordJsonList (String medicalRecords) throws JsonProcessingException {
		return objectMapper.readValue(medicalRecords, new TypeReference<List<MedicalRecordJson>>() {});
	}

	private List<FireStationJson> mapFireStationsStringToFireStationJsonList (String fireStations) throws JsonProcessingException {
		return objectMapper.readValue(fireStations, new TypeReference<List<FireStationJson>>() {});
	}

	private List<Address> mapPersonJsonListToAddressWithResidentsList (List<PersonJson> personsJson) {
		List<Address> addresses = new ArrayList<>();

		personsJson.forEach(personJson -> {
			Person person = new Person(personJson.getFirstName(), personJson.getLastName(), personJson.getPhone(), personJson.getEmail());

			Address address;

			List<Address> filteredAddresses = addresses.stream().filter(a -> a.getLabel().equals(personJson.getAddress()) && a.getZip().equals(personJson.getZip()) && a.getCity().equals(personJson.getCity()) ).toList();

			if (filteredAddresses.size() > 0) {
				address = filteredAddresses.get(0);
			} else {
				address = new Address(personJson.getAddress(), personJson.getZip(), personJson.getCity());
				addresses.add(address);
			}

			address.addResident(person);
		});

		return addresses;
	}

	private List<Address> mapAddressEntitiesForIncludeFireStations (List<Address> addresses, List<FireStationJson> fireStationsJson) {
		fireStationsJson.forEach(fireStationJson -> {
			String fireStationAddress = fireStationJson.getAddress();

			for (Address address : addresses) {
				if (address.getLabel().equals(fireStationAddress)) {
					FireStation fireStation = new FireStation(Integer.parseInt(fireStationJson.getStation()));
					address.addFireStation(fireStation);
				}
			}
		});

		return addresses;
	}

	private List<MedicalRecord> mapMedicalRecordJsonListToMedicalRecordList (List<MedicalRecordJson> medicalRecordsJson) {
		List<MedicalRecord> medicalRecords = new ArrayList<>();

		List<Allergy> allergies = new ArrayList<>();
		List<Medication> medications = new ArrayList<>();

		medicalRecordsJson.forEach(m -> {
			String[] birthdaySplit= m.getBirthdate().split("/");
			String birthdayFormatted = birthdaySplit[2] + "-" + birthdaySplit[0] + "-" + birthdaySplit[1];
			LocalDate birthday = LocalDate.parse(birthdayFormatted);

			MedicalRecord medicalRecord = new MedicalRecord(m.getFirstName(), m.getLastName(), birthday);

			List<Allergy> currentAllergies = new ArrayList<>();
			for (String allergyString : m.getAllergies()) {
				List<Allergy> filteredAllergies = allergies.stream().filter(a -> a.getName().equalsIgnoreCase(allergyString)).toList();

				if (filteredAllergies.size() > 0) {
					currentAllergies.add(filteredAllergies.get(0));
				} else {
					Allergy newAllergy = new Allergy(allergyString);
					newAllergy.setId(allergies.size() + 1);
					allergies.add(newAllergy);
					currentAllergies.add(newAllergy);
				}
			}
			medicalRecord.setAllergies(currentAllergies);

			List<Medication> currentMedications = new ArrayList<>();
			for (String medicationString : m.getMedications()) {
				String[] medicationStringSplit = medicationString.split(":");
				String name = medicationStringSplit[0];
				int dosage = Integer.parseInt(medicationStringSplit[1].substring(0, (medicationStringSplit[1].length() - 2)));

				List<Medication> filteredMedication = medications.stream().filter(medication -> medication.getName().equalsIgnoreCase(name) && medication.getDosage() == dosage).toList();

				if (filteredMedication.size() > 0) {
					currentMedications.add(filteredMedication.get(0));
				} else {
					Medication medication = new Medication(name, dosage);
					medication.setId(medications.size() + 1);
					currentMedications.add(medication);
					medications.add(medication);
				}
			}
			medicalRecord.setMedications(currentMedications);

			medicalRecords.add(medicalRecord);
		});

		return medicalRecords;
	}

	public List<Address> getAddresses () {
		String personsString = jsonReader.getPersonsJson();
		String fireStationsString = jsonReader.getFirestationsData();

		List<PersonJson> personsJsonObjects;
		List<FireStationJson> fireStationsJsonObjects;

		try {
			personsJsonObjects = mapPersonsStringToPersonJsonList(personsString);
			fireStationsJsonObjects = mapFireStationsStringToFireStationJsonList(fireStationsString);
		} catch (JsonProcessingException e) {
			System.out.println(e);
			personsJsonObjects = new ArrayList<>();
			fireStationsJsonObjects = new ArrayList<>();
		}

		List<Address> addressesWithResident = mapPersonJsonListToAddressWithResidentsList(personsJsonObjects);
		List<Address> addressesWithResidentAndFireStation = mapAddressEntitiesForIncludeFireStations(addressesWithResident, fireStationsJsonObjects);

		return addressesWithResidentAndFireStation;
	}

	public List<MedicalRecord> getMedicalRecords () {
		String medicalRecordsString = jsonReader.getMedicalrecordsData();

		List<MedicalRecordJson> medicalRecordsJsonObjects;

		try {
			medicalRecordsJsonObjects = mapMedicalRecordsStringToMedicalRecordJsonList(medicalRecordsString);
		} catch (JsonProcessingException e) {
			System.out.println(e);
			medicalRecordsJsonObjects = new ArrayList<>();
		}

		List<MedicalRecord> medicalRecords = mapMedicalRecordJsonListToMedicalRecordList(medicalRecordsJsonObjects);

		return medicalRecords;
	}

}
