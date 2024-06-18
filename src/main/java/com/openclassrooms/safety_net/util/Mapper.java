package com.openclassrooms.safety_net.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safety_net.model.dto.GlobalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonMapper {
	@Autowired
	private ObjectMapper objectMapper;

	private String dataJson;

	{
		dataJson = JsonReader.getDataJson();
	}

	public GlobalDTO mapDataToGlobalDTO () throws JsonProcessingException {
		return objectMapper.readValue(dataJson, GlobalDTO.class);
	}

	/*private List<PersonDTO> mapPersonsStringToPersonJsonList (String persons) throws JsonProcessingException {
		return objectMapper.readValue(persons, new TypeReference<List<PersonDTO>>() {});
	}

	private List<MedicalRecordDTO> mapMedicalRecordsStringToMedicalRecordJsonList (String medicalRecords) throws JsonProcessingException {
		return objectMapper.readValue(medicalRecords, new TypeReference<List<MedicalRecordDTO>>() {});
	}

	private List<FireStationDTO> mapFireStationsStringToFireStationJsonList (String fireStations) throws JsonProcessingException {
		return objectMapper.readValue(fireStations, new TypeReference<List<FireStationDTO>>() {});
	}

	private List<Address> mapPersonJsonListToAddressWithResidentsList (List<PersonDTO> personsJson) {
		List<Address> addresses = new ArrayList<>();

		personsJson.forEach(personDTO -> {
			Person person = new Person(personDTO.getFirstName(), personDTO.getLastName(), personDTO.getPhone(), personDTO.getEmail());

			Address address;

			List<Address> filteredAddresses = addresses.stream().filter(a -> a.getLabel().equals(personDTO.getAddress()) && a.getZip().equals(personDTO.getZip()) && a.getCity().equals(personDTO.getCity()) ).toList();

			if (filteredAddresses.size() > 0) {
				address = filteredAddresses.get(0);
			} else {
				address = new Address(personDTO.getAddress(), personDTO.getZip(), personDTO.getCity());
				addresses.add(address);
			}

			address.addResident(person);
		});

		return addresses;
	}

	private List<Address> mapAddressEntitiesForIncludeFireStations (List<Address> addresses, List<FireStationDTO> fireStationsJson) {
		fireStationsJson.forEach(fireStationDTO -> {
			String fireStationAddress = fireStationDTO.getAddress();

			for (Address address : addresses) {
				if (address.getLabel().equals(fireStationAddress)) {
					FireStation fireStation = new FireStation(Integer.parseInt(fireStationDTO.getStation()));
					address.addFireStation(fireStation);
				}
			}
		});

		return addresses;
	}

	private List<MedicalRecord> mapMedicalRecordJsonListToMedicalRecordList (List<MedicalRecordDTO> medicalRecordsJson) {
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

		List<PersonDTO> personsJsonObjects;
		List<FireStationDTO> fireStationsJsonObjects;

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

		List<MedicalRecordDTO> medicalRecordsJsonObjects;

		try {
			medicalRecordsJsonObjects = mapMedicalRecordsStringToMedicalRecordJsonList(medicalRecordsString);
		} catch (JsonProcessingException e) {
			System.out.println(e);
			medicalRecordsJsonObjects = new ArrayList<>();
		}

		List<MedicalRecord> medicalRecords = mapMedicalRecordJsonListToMedicalRecordList(medicalRecordsJsonObjects);

		return medicalRecords;
	}*/

}
