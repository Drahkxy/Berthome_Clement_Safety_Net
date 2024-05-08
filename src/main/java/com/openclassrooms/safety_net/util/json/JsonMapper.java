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
import java.util.Map;

@Component
public class JsonMapper {
	@Autowired
	private JsonReader jsonReader;
	private final ObjectMapper objectMapper = new ObjectMapper();


	private List<PersonJson> mapPersonsJsonToPersonsJsonObject(String personsJson) throws JsonProcessingException {
		return objectMapper.readValue(personsJson, new TypeReference<List<PersonJson>>() {});
	}

	private List<MedicalRecordJson> mapMedicalRecordsJsonToMedicalRecordJsonObject(String medicalRecordsJson) throws JsonProcessingException {
		return objectMapper.readValue(medicalRecordsJson, new TypeReference<List<MedicalRecordJson>>() {});
	}

	private List<FireStationJson> mapFireStationJsonToFireStationJsonObject(String fireStationsJson) throws JsonProcessingException {
		return objectMapper.readValue(fireStationsJson, new TypeReference<List<FireStationJson>>() {});
	}

	public Map.Entry<String, Integer> mapMedicationJsonToMedicationObjects(String medicationJson) {
		var split = medicationJson.split(":");
		Map.Entry<String, Integer> medicationEntry = Map.entry(split[0], Integer.valueOf(split[1].substring(0, (split[1].length() - 2))));
		return medicationEntry;
	}

	private List<Address> mapAddressEntitiesForIncludeFireStations(List<Address> addresses, List<FireStationJson> fireStationsJson) {
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


	private List<Address> mapPersonsJsonObjectToAddressEntity(List<PersonJson> personsJson, List<MedicalRecordJson> medicalRecordsJson) {
		List<Address> addresses = new ArrayList<>();
		List<Allergy> allergies = new ArrayList<>();

		personsJson.forEach(personJson -> {
			Person person = new Person(personJson.getFirstName(), personJson.getLastName(), personJson.getPhone(), personJson.getEmail());

			//Récupération du dossier médical de la personne, création de l'instance de l'entité Person et ajout des données du dossier médical à l'instance
			for (MedicalRecordJson medicalRecord : medicalRecordsJson) {
				if (medicalRecord.getFirstName().equals( personJson.getFirstName()) && medicalRecord.getLastName().equals(personJson.getLastName())) {

					//Récupération de la date de naissance au sein du dossier médical correspondant à la personne et création de l'instace Person.
					String[] birthdayData = medicalRecord.getBirthdate().split("/");
					String bithdayString = birthdayData[2] + "-" + birthdayData[0] + "-" +birthdayData[1];
					LocalDate birthday = LocalDate.parse(bithdayString);
					person.setBirthday(birthday);

					//Récupération des traitements pris par la personne au sein de son dossier médical, formatage de ces traitements et enregistrement au sein de l'instance de Person
					String[] medications = medicalRecord.getMedications();
					for (String medication : medications) {
						Map.Entry<String, Integer> medicationEntry = mapMedicationJsonToMedicationObjects(medication);
						Medication medicationObject = new Medication(medicationEntry.getKey(), medicationEntry.getValue());
						person.addMedication(medicationObject);
					}

					//Récupération des allergies de la personne au sein de son dossier médical et enregistrement des allergies récupérées au sein de l'instance de Person
					String[] allergiesArray = medicalRecord.getAllergies();
					for (String allergy : allergiesArray) {
						var allergyFiltered = allergies.stream().filter(a -> a.getName().equals(allergy)).toList();
						Allergy allergyObject;

						if (allergyFiltered.size() == 0) {
							allergyObject = new Allergy(allergy);
						} else {
							allergyObject = allergyFiltered.get(0);
						}

						person.addAllergy(allergyObject);
					}
				}
			}

			var addressesFiltered = addresses.stream().filter(a -> a.getCity().equals(personJson.getCity()) && a.getZip().equals(personJson.getZip()) && a.getLabel().equals(personJson.getAddress())).toList();

			if (addressesFiltered.size() == 1) {
				Address address = addressesFiltered.get(0);
				address.addResident(person);
			} else {
				Address address = new Address(personJson.getAddress(), personJson.getZip(), personJson.getCity());
				address.addResident(person);
				addresses.add(address);
			}
		});

		return addresses;
	}

	public List<Address> getAddressEntitiesWithResidents() {
		String personsJson = jsonReader.getPersonsJson();
		String medicalRecordsJson = jsonReader.getMedicalrecordsData();
		String fireStationsJson = jsonReader.getFirestationsData();

		List<PersonJson> personsJsonObjects;
		List<MedicalRecordJson> medicalRecordsJsonObjects;
		List<FireStationJson> fireStationsJsonObjects;

		try {
			personsJsonObjects = mapPersonsJsonToPersonsJsonObject(personsJson);
			medicalRecordsJsonObjects = mapMedicalRecordsJsonToMedicalRecordJsonObject(medicalRecordsJson);
			fireStationsJsonObjects = mapFireStationJsonToFireStationJsonObject(fireStationsJson);
		} catch (JsonProcessingException e) {
			System.out.println(e);
			personsJsonObjects = new ArrayList<>();
			medicalRecordsJsonObjects = new ArrayList<>();
			fireStationsJsonObjects = new ArrayList<>();
		}

		List<Address> addressesWithResident = mapPersonsJsonObjectToAddressEntity(personsJsonObjects, medicalRecordsJsonObjects);
		List<Address> addressesWithResidentAndFireStation = mapAddressEntitiesForIncludeFireStations(addressesWithResident, fireStationsJsonObjects);

		return addressesWithResidentAndFireStation;
	}


}
