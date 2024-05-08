package com.openclassrooms.safety_net.util.json;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class JsonReader {
	public String getPersonsJson() {
		//List<PersonJson> persons;
		String personsJson;
		try {
			ClassPathResource personsJsonResource = new ClassPathResource("persons.json");
			personsJson = Files.readString(Path.of(personsJsonResource.getURI()));
			//persons = objectMapper.readValue(personsJson, new TypeReference<List<PersonJson>>() {});
		} catch (IOException e) {
			personsJson = "";
		}
		return personsJson;
	}

	public String getFirestationsData() {
		//List<FireStationJson> fireStations;
		String firestationsJson;
		try {
			ClassPathResource firestationsJsonResource = new ClassPathResource("firestations.json");
			firestationsJson = Files.readString(Path.of(firestationsJsonResource.getURI()));
			//fireStations = objectMapper.readValue(firestationsJson, new TypeReference<List<FireStationJson>>() {});
		} catch (IOException e) {
			firestationsJson = "";
			//fireStations = new ArrayList<>();
		}
		return firestationsJson;
		//return fireStations;
	}

	public String getMedicalrecordsData() {
		//List<MedicalRecordJson> medicalRecords;
		String medicalrecordsJson;
		try {
			ClassPathResource medicalrecordsJsonResource = new ClassPathResource("medicalrecords.json");
			medicalrecordsJson = Files.readString(Path.of(medicalrecordsJsonResource.getURI()));
			//medicalRecords = objectMapper.readValue(medicalrecordsJson, new TypeReference<List<MedicalRecordJson>>() {});
		} catch (IOException e) {
			medicalrecordsJson = "";
			//medicalRecords = new ArrayList<>();
		}
		return medicalrecordsJson;
		//return medicalRecords;

	}
}
