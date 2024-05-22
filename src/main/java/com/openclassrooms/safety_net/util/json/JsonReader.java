package com.openclassrooms.safety_net.util.json;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class JsonReader {
	public String getPersonsJson() {
		String personsJson;
		try {
			ClassPathResource personsJsonResource = new ClassPathResource("persons.json");
			personsJson = Files.readString(Path.of(personsJsonResource.getURI()));
		} catch (IOException e) {
			personsJson = "";
		}
		return personsJson;
	}

	public String getFirestationsData() {
		String firestationsJson;
		try {
			ClassPathResource firestationsJsonResource = new ClassPathResource("firestations.json");
			firestationsJson = Files.readString(Path.of(firestationsJsonResource.getURI()));
		} catch (IOException e) {
			firestationsJson = "";
		}
		return firestationsJson;
	}

	public String getMedicalrecordsData() {
		String medicalrecordsJson;
		try {
			ClassPathResource medicalrecordsJsonResource = new ClassPathResource("medicalrecords.json");
			medicalrecordsJson = Files.readString(Path.of(medicalrecordsJsonResource.getURI()));
		} catch (IOException e) {
			medicalrecordsJson = "";
		}
		return medicalrecordsJson;
	}

}
