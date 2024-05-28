package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.*;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UtilitiesServiceTest {
	@Mock
	private PersonService personService;
	@Mock
	private MedicalRecordService medicalRecordService;
	@Mock
	private FireStationService fireStationService;
	@Mock
	private AddressService addressService;
	@InjectMocks
	private UtilitiesService utilitiesService;

	private PersonId  personId;
	private Person person;
	private MedicalRecord medicalRecord;
	private FireStation fireStation;
	private Address address;
	@BeforeEach
	public void setUp () {
		personId = new PersonId("John", "Doe");

		address = new Address("123 Main St", "12345", "Springfield");

		person = new Person("John", "Doe", "0123456789", "john.doe@example.com");

		fireStation = new FireStation(1);
		fireStation.setId(1);

		address.addResident(person);
		address.addFireStation(fireStation);

		medicalRecord = new MedicalRecord(
				personId.getFirstName(),
				personId.getLastName(),
				LocalDate.of(1996, 3, 9)
		);
		List<Allergy> allergies = List.of(new Allergy("peanut"));
		medicalRecord.setAllergies(allergies);
		List<Medication> medications = List.of(new Medication("aznol", 100));
		medicalRecord.setMedications(medications);
	}

	@Test
	public void testGetPersonInfo() {
		when(personService.getPersonById(personId)).thenReturn(person);
		when(medicalRecordService.getMedicalRecordById(personId)).thenReturn(medicalRecord);

		PersonInfosNameEmailAgeAddressMedicals result = utilitiesService.getPersonInfo("John", "Doe");

		assertNotNull(result);
		assertEquals(person.getFirstName(), result.getFirstName());
		assertEquals(person.getLastName(), result.getLastName());
		assertEquals(person.getEmail(), result.getEmail());
		assertEquals(medicalRecord.getAge(), result.getAge());
		assertEquals(person.getAddress(), result.getAddress());
		assertEquals(medicalRecord.getAllergies(), result.getAllergies());
		assertEquals(medicalRecord.getMedications(), result.getMedications());
	}

	@Test
	public void testGetHomesResidentsInformationsCoveredByFireStations() {
		when(fireStationService.getFireStationsByStationNumber(1)).thenReturn(List.of(fireStation));
		when(medicalRecordService.getMedicalRecordById(personId)).thenReturn(medicalRecord);

		List<AddressInfos> result = utilitiesService.getHomesResidentsInformationsCoveredByFireStations(1);

		assertNotNull(result);
		assertFalse(result.isEmpty());

		AddressInfos addressInfos = result.get(0);
		assertEquals(address.getLabel(), addressInfos.getLabel());
		assertEquals(address.getZip(), addressInfos.getZip());
		assertEquals(address.getCity(), addressInfos.getCity());

		assertNotNull(addressInfos.getResidentsInfos());
		assertFalse(addressInfos.getResidentsInfos().isEmpty());

		PersonInfosNameEmailAgeAddressMedicals personInfos = addressInfos.getResidentsInfos().get(0);
		assertEquals(person.getFirstName(), personInfos.getFirstName());
		assertEquals(person.getLastName(), personInfos.getLastName());
		assertEquals(person.getEmail(), personInfos.getEmail());
		assertEquals(medicalRecord.getAge(), personInfos.getAge());
		assertEquals(person.getAddress(), personInfos.getAddress());
		assertEquals(medicalRecord.getAllergies(), personInfos.getAllergies());
		assertEquals(medicalRecord.getMedications(), personInfos.getMedications());
	}

	@Test
	public void testGetFireInfos() {
		when(addressService.getAddressByLabelAndZipAndCity(address.getLabel(), address.getZip(), address.getCity()))
				.thenReturn(address);

		when(medicalRecordService.getMedicalRecordById(personId)).thenReturn(medicalRecord);

		FireInfos result = utilitiesService.getFireInfos("123 Main St", "12345", "Springfield");

		assertNotNull(result);

		assertNotNull(result.getFireStationsNumber());
		assertFalse(result.getFireStationsNumber().isEmpty());
		assertEquals(fireStation.getStation(), result.getFireStationsNumber().get(0));

		assertNotNull(result.getResidentsInfos());
		assertFalse(result.getResidentsInfos().isEmpty());

		PersonInfosNameEmailAgeAddressMedicals personInfos = result.getResidentsInfos().get(0);
		assertEquals(person.getFirstName(), personInfos.getFirstName());
		assertEquals(person.getLastName(), personInfos.getLastName());
		assertEquals(person.getEmail(), personInfos.getEmail());
		assertEquals(medicalRecord.getAge(), personInfos.getAge());
		assertEquals(person.getAddress(), personInfos.getAddress());
		assertEquals(medicalRecord.getAllergies(), personInfos.getAllergies());
		assertEquals(medicalRecord.getMedications(), personInfos.getMedications());
	}

	@Test
	public void testGetPhoneAlertInfos() {
		when(fireStationService.getFireStationById(fireStation.getId())).thenReturn(fireStation);

		List<String> result = utilitiesService.getPhoneAlertInfos(fireStation.getId());

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(person.getPhone(), result.get(0));
	}

	@Test
	public void testGetChildAlertInfos() {
		PersonId childId = new PersonId("Jane", "Doe");

		Person child = new Person(childId.getFirstName(), childId.getLastName(), "9876543210", "doe.jane@exemple.fr");
		address.addResident(child);

		MedicalRecord childRecord = new MedicalRecord(
				childId.getFirstName(),
				childId.getLastName(),
				LocalDate.of(2017, 6, 23)
		);
		childRecord.setMedications(medicalRecord.getMedications());
		childRecord.setAllergies(medicalRecord.getAllergies());


		when(addressService.getAddressByLabelAndZipAndCity("123 Main St", "12345", "Springfield"))
				.thenReturn(address);

		when(medicalRecordService.getMedicalRecordById(personId)).thenReturn(medicalRecord);
		when(medicalRecordService.getMedicalRecordById(childId)).thenReturn(childRecord);


		List<ChildInfos> result = utilitiesService.getChildAlertInfos(
				"123 Main St",
				"12345",
				"Springfield"
		);


		assertNotNull(result);
		assertFalse(result.isEmpty());

		ChildInfos childInfos = result.get(0);
		assertEquals(child.getFirstName(), childInfos.getFirstName());
		assertEquals(child.getLastName(), childInfos.getLastName());
		assertEquals(childRecord.getAge(), childInfos.getAge());

		assertNotNull(childInfos.getFamilyMembersCompleteName());
		assertFalse(childInfos.getFamilyMembersCompleteName().isEmpty());
		assertEquals(person.getFirstName() + " " + person.getLastName(), childInfos.getFamilyMembersCompleteName().get(0));
	}

	@Test
	public void testGetPersonsCoveredByFireStationsInfos() {
		PersonId childId = new PersonId("Jane", "Doe");

		Person child = new Person(childId.getFirstName(), childId.getLastName(), "9876543210", "doe.jane@exemple.fr");
		address.addResident(child);

		MedicalRecord childRecord = new MedicalRecord(
				childId.getFirstName(),
				childId.getLastName(),
				LocalDate.of(2007, 6, 23)
		);
		childRecord.setMedications(medicalRecord.getMedications());
		childRecord.setAllergies(medicalRecord.getAllergies());


		when(fireStationService.getFireStationsByStationNumber(fireStation.getStation()))
				.thenReturn(List.of(fireStation));

		when(medicalRecordService.getMedicalRecordById(personId)).thenReturn(medicalRecord);
		when(medicalRecordService.getMedicalRecordById(childId)).thenReturn(childRecord);


		PersonsCoveredByFireStationsInfos result = utilitiesService.getPersonsCoveredByFireStationsInfos(1);


		assertNotNull(result);
		assertEquals(1, result.getAdultCount());
		assertEquals(1, result.getChildrenCount());

		assertNotNull(result.getPersons());
		assertFalse(result.getPersons().isEmpty());
		assertEquals(2, result.getPersons().size());
	}

}
