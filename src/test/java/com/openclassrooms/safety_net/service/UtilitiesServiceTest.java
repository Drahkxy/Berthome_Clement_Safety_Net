package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.*;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
	public void getPersonInfo_success_test () {
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

		verify(personService, times(1)).getPersonById(personId);
		verify(medicalRecordService, times(1)).getMedicalRecordById(personId);
	}

	@Test
	public void getHomesResidentsInformationsCoveredByFireStations_success_test () {
		when(fireStationService.getFireStationsByStationNumber(fireStation.getStation())).thenReturn(List.of(fireStation));
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

		verify(fireStationService, times(1)).getFireStationsByStationNumber(fireStation.getStation());
		verify(medicalRecordService, times(1)).getMedicalRecordById(personId);
	}

	@Test
	public void getFireInfos_addressFound_test () {
		when(addressService.getAddressByLabelAndZipAndCity(address.getLabel(), address.getZip(), address.getCity()))
				.thenReturn(address);

		when(medicalRecordService.getMedicalRecordById(personId)).thenReturn(medicalRecord);

		FireInfos result = utilitiesService.getFireInfos(address.getLabel(), address.getZip(), address.getCity());

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

		verify(addressService, times(1))
				.getAddressByLabelAndZipAndCity(address.getLabel(), address.getZip(), address.getCity());
		verify(medicalRecordService, times(1)).getMedicalRecordById(personId);
	}

	@Test
	public void getFireInfos_addressNotFound_test () {
		when(addressService.getAddressByLabelAndZipAndCity(address.getLabel(), address.getZip(), address.getCity()))
				.thenReturn(null);

		ResponseStatusException exception  = assertThrows(ResponseStatusException.class, () -> {
			utilitiesService.getFireInfos(address.getLabel(), address.getZip(), address.getCity());
		});

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

		verify(addressService, times(1))
				.getAddressByLabelAndZipAndCity(address.getLabel(), address.getZip(), address.getCity());
		verify(medicalRecordService, never()).getMedicalRecordById(any(PersonId.class));
	}

	@Test
	public void getPhoneAlertInfos_success_test () {
		when(fireStationService.getFireStationById(fireStation.getId())).thenReturn(fireStation);

		List<String> result = utilitiesService.getPhoneAlertInfos(fireStation.getId());

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(person.getPhone(), result.get(0));

		verify(fireStationService, times(1)).getFireStationById(fireStation.getId());
	}

	@Test
	public void getChildAlertInfos_success_test () {
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

		verify(addressService, times(1))
				.getAddressByLabelAndZipAndCity("123 Main St", "12345", "Springfield");
		verify(medicalRecordService, times(2)).getMedicalRecordById(any(PersonId.class));
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

		verify(fireStationService, times(1))
				.getFireStationsByStationNumber(fireStation.getStation());
		verify(medicalRecordService, times(2)).getMedicalRecordById(any(PersonId.class));
	}

}
