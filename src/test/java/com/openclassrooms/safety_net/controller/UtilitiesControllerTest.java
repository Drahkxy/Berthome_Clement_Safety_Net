package com.openclassrooms.safety_net.controller;

import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.response.*;
import com.openclassrooms.safety_net.service.PersonService;
import com.openclassrooms.safety_net.service.UtilitiesService;
import com.openclassrooms.safety_net.util.DbSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(controllers = UtilitiesController.class)
public class UtilitiesControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private DbSetup dbSetup;
	@MockBean
	private UtilitiesService utilitiesService;
	@MockBean
	private PersonService personService;

	private static String existingAddressCity;
	private static String nonExistentAddressCity;
	private static Address existingAddress;
	private static Address nonExistentAddress;
	private static int existingStationNumber;
	private static int nonExistentStationNumber;
	private static RuntimeException runtimeException;
	private static ResponseStatusException responseStatusExceptionNotFound;

	@BeforeAll
	public static void setUp () {
		existingAddressCity = "Springfield";
		nonExistentAddressCity = "Shelbyville";

		existingAddress = new Address("123 Main St", "12345", existingAddressCity);
		nonExistentAddress = new Address("459 Elm St", "67890", nonExistentAddressCity);

		existingStationNumber = 1;
		nonExistentStationNumber = 2;

		runtimeException = new RuntimeException("Service exception.");
		responseStatusExceptionNotFound = new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found.");
	}


	@Test
	public void getPersonsCityMails_success_test () throws Exception {
		List<String> emails = List.of("john.doe@exemple.com", "doe.jane@exemple.fr");

		when(personService.getPersonsEmailByCity(existingAddressCity)).thenReturn(emails);

		mockMvc.perform(get("/communityEmail?city=%s".formatted(existingAddressCity)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0]").value(emails.get(0)))
				.andExpect(jsonPath("$[1]").value(emails.get(1)));

		verify(personService, times(1)).getPersonsEmailByCity(existingAddressCity);
	}

	@Test
	public void getPersonsCityMails_throwsException_test () throws Exception {
		when(personService.getPersonsEmailByCity(nonExistentAddressCity)).thenThrow(runtimeException);

		mockMvc.perform(get("/communityEmail?city=%s".formatted(nonExistentAddressCity)))
				.andExpect(status().isInternalServerError());

		verify(personService, times(1)).getPersonsEmailByCity(nonExistentAddressCity);
	}

	@Test
	public void getPersonsCityMails_throwsResponseStatusException_test () throws Exception {
		when(personService.getPersonsEmailByCity(nonExistentAddressCity)).thenThrow(responseStatusExceptionNotFound);


		mockMvc.perform(get("/communityEmail?city=%s".formatted(nonExistentAddressCity)))
				.andExpect(status().isNotFound());

		verify(personService, times(1)).getPersonsEmailByCity(nonExistentAddressCity);
	}

	@Test
	public void getPersonInfo_success_test () throws Exception {
		PersonId personId = new PersonId("John", "Doe");
		PersonInfosNameEmailAgeAddressMedicals personInfos = new PersonInfosNameEmailAgeAddressMedicals();

		when(utilitiesService.getPersonInfo(personId.getFirstName(), personId.getLastName()))
				.thenReturn(personInfos);

		mockMvc.perform(
				get("/personInfo?firstName=%s&lastName=%s".formatted(personId.getFirstName(), personId.getLastName()))
		)
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(utilitiesService, times(1))
				.getPersonInfo(personId.getFirstName(), personId.getLastName());
	}

	@Test
	public void getPersonInfo_throwsException_test () throws Exception {
		PersonId personId = new PersonId("Jane", "Doe");

		when(utilitiesService.getPersonInfo(personId.getFirstName(), personId.getLastName()))
				.thenThrow(runtimeException);

		mockMvc.perform(
				get("/personInfo?firstName=%s&lastName=%s".formatted(personId.getFirstName(), personId.getLastName()))
		)
		.andExpect(status().isInternalServerError());

		verify(utilitiesService, times(1))
				.getPersonInfo(personId.getFirstName(), personId.getLastName());
	}

	@Test
	public void getPersonInfo_throwsResponseStatusException_test () throws Exception {
		PersonId personId = new PersonId("Jane", "Doe");

		when(utilitiesService.getPersonInfo(personId.getFirstName(), personId.getLastName()))
				.thenThrow(responseStatusExceptionNotFound);

		mockMvc.perform(
				get("/personInfo?firstName=%s&lastName=%s".formatted(personId.getFirstName(), personId.getLastName()))
		)
				.andExpect(status().isNotFound());

		verify(utilitiesService, times(1))
				.getPersonInfo(personId.getFirstName(), personId.getLastName());
	}

	@Test
	public void getHomesResidentsInformationsCoveredByFireStations_success_test () throws Exception {
		List<AddressInfos> addressInfos = List.of(new AddressInfos(), new AddressInfos());

		when(utilitiesService.getHomesResidentsInformationsCoveredByFireStations(existingStationNumber))
				.thenReturn(addressInfos);

		mockMvc.perform(get("/flood/stations?stations=%d".formatted(existingStationNumber)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(utilitiesService, times(1))
				.getHomesResidentsInformationsCoveredByFireStations(existingStationNumber);
	}

	@Test
	public void getHomesResidentsInformationsCoveredByFireStations_throwsException_test () throws Exception {
		when(utilitiesService.getHomesResidentsInformationsCoveredByFireStations(nonExistentStationNumber))
				.thenThrow(runtimeException);

		mockMvc.perform(get("/flood/stations?stations=%d".formatted(nonExistentStationNumber)))
				.andExpect(status().isInternalServerError());

		verify(utilitiesService, times(1))
				.getHomesResidentsInformationsCoveredByFireStations(nonExistentStationNumber);
	}

	@Test
	public void getHomesResidentsInformationsCoveredByFireStations_throwsResponseStatusException_test () throws Exception {
		when(utilitiesService.getHomesResidentsInformationsCoveredByFireStations(nonExistentStationNumber))
				.thenThrow(responseStatusExceptionNotFound);

		mockMvc.perform(get("/flood/stations?stations=%d".formatted(nonExistentStationNumber)))
				.andExpect(status().isNotFound());

		verify(utilitiesService, times(1))
				.getHomesResidentsInformationsCoveredByFireStations(nonExistentStationNumber);
	}

	@Test
	public void getFireInfos_success_test () throws Exception {
		FireInfos fireInfos = new FireInfos();

		when(utilitiesService.getFireInfos(
				existingAddress.getLabel(),
				existingAddress.getZip(),
				existingAddress.getCity()
		))
		.thenReturn(fireInfos);

		mockMvc.perform(get("/fire?label=%s&zip=%s&city=%s".formatted(
				existingAddress.getLabel(),
				existingAddress.getZip(),
				existingAddress.getCity()
		)))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(utilitiesService, times(1))
				.getFireInfos(existingAddress.getLabel(), existingAddress.getZip(), existingAddress.getCity());
	}

	@Test
	public void getFireInfos_throwsException_test () throws Exception {
		when(utilitiesService.getFireInfos(
				nonExistentAddress.getLabel(),
				nonExistentAddress.getZip(),
				nonExistentAddress.getCity()
		))
		.thenThrow(runtimeException);

		mockMvc.perform(
				get("/fire?label=%s&zip=%s&city=%s".formatted(
						nonExistentAddress.getLabel(),
						nonExistentAddress.getZip(),
						nonExistentAddress.getCity()
				))
		)
		.andExpect(status().isInternalServerError());

		verify(utilitiesService, times(1))
				.getFireInfos(nonExistentAddress.getLabel(), nonExistentAddress.getZip(), nonExistentAddress.getCity());
	}

	@Test
	public void getFireInfos_throwsResponseStatusException_test () throws Exception {
		when(utilitiesService.getFireInfos(
				nonExistentAddress.getLabel(),
				nonExistentAddress.getZip(),
				nonExistentAddress.getCity()
		))
		.thenThrow(responseStatusExceptionNotFound);

		mockMvc.perform(
				get("/fire?label=%s&zip=%s&city=%s".formatted(
						nonExistentAddress.getLabel(),
						nonExistentAddress.getZip(),
						nonExistentAddress.getCity()
				))
		)
		.andExpect(status().isNotFound());

		verify(utilitiesService, times(1))
				.getFireInfos(nonExistentAddress.getLabel(), nonExistentAddress.getZip(), nonExistentAddress.getCity());
	}

	@Test
	public void getPhoneAlertInfos_success_test () throws Exception {
		List<String> phones = List.of("0123456789", "9876543210");

		when(utilitiesService.getPhoneAlertInfos(existingStationNumber)).thenReturn(phones);

		mockMvc.perform(get("/phoneAlert?firestation=%s".formatted(existingStationNumber)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(utilitiesService, times(1)).getPhoneAlertInfos(existingStationNumber);
	}

	@Test
	public void getPhoneAlertInfos_throwsException_test () throws Exception {
		when(utilitiesService.getPhoneAlertInfos(nonExistentStationNumber))
				.thenThrow(runtimeException);

		mockMvc.perform(get("/phoneAlert?firestation=%s".formatted(nonExistentStationNumber)))
				.andExpect(status().isInternalServerError());

		verify(utilitiesService, times(1)).getPhoneAlertInfos(nonExistentStationNumber);
	}

	@Test
	public void getPhoneAlertInfos_throwsResponseStatusException_test () throws Exception {
		when(utilitiesService.getPhoneAlertInfos(nonExistentStationNumber))
				.thenThrow(responseStatusExceptionNotFound);

		mockMvc.perform(get("/phoneAlert?firestation=%s".formatted(nonExistentStationNumber)))
				.andExpect(status().isNotFound());

		verify(utilitiesService, times(1)).getPhoneAlertInfos(nonExistentStationNumber);
	}
	@Test
	public void getChildAlertInfos_success_test () throws Exception {
		List<ChildInfos> childInfos = List.of(new ChildInfos(), new ChildInfos());

		when(utilitiesService.getChildAlertInfos(
				existingAddress.getLabel(),
				existingAddress.getZip(),
				existingAddress.getCity()
		))
		.thenReturn(childInfos);

		mockMvc.perform(
				get("/childAlert?label=%s&zip=%s&city=%s".formatted(
						existingAddress.getLabel(),
						existingAddress.getZip(),
						existingAddress.getCity()
				))
		)
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(utilitiesService, times(1))
				.getChildAlertInfos(existingAddress.getLabel(), existingAddress.getZip(), existingAddress.getCity());
	}

	@Test
	public void getChildAlertInfos_throwsException_test () throws Exception {
		when(utilitiesService.getChildAlertInfos(
				nonExistentAddress.getLabel(),
				nonExistentAddress.getZip(),
				nonExistentAddress.getCity()
		))
		.thenThrow(runtimeException);

		mockMvc.perform(
				get("/childAlert?label=%s&zip=%s&city=%s".formatted(
						nonExistentAddress.getLabel(),
						nonExistentAddress.getZip(),
						nonExistentAddress.getCity()
				))
		)
				.andExpect(status().isInternalServerError());

		verify(utilitiesService, times(1))
				.getChildAlertInfos(
						nonExistentAddress.getLabel(),
						nonExistentAddress.getZip(),
						nonExistentAddress.getCity()
				);
	}

	@Test
	public void getChildAlertInfos_throwsResponseStatusException_test () throws Exception {
		when(utilitiesService.getChildAlertInfos(
				nonExistentAddress.getLabel(),
				nonExistentAddress.getZip(),
				nonExistentAddress.getCity()
		))
		.thenThrow(responseStatusExceptionNotFound);

		mockMvc.perform(
				get("/childAlert?label=%s&zip=%s&city=%s".formatted(
						nonExistentAddress.getLabel(),
						nonExistentAddress.getZip(),
						nonExistentAddress.getCity()))
		)
		.andExpect(status().isNotFound());

		verify(utilitiesService, times(1))
				.getChildAlertInfos(
						nonExistentAddress.getLabel(),
						nonExistentAddress.getZip(),
						nonExistentAddress.getCity()
				);
	}

	@Test
	public void getPersonsCoveredByFireStationsInfos_success_test () throws Exception {
		PersonsCoveredByFireStationsInfos personsCoveredByFireStationsInfos = new PersonsCoveredByFireStationsInfos();

		when(utilitiesService.getPersonsCoveredByFireStationsInfos(existingStationNumber))
				.thenReturn(personsCoveredByFireStationsInfos);

		mockMvc.perform(get("/firestations_residents?stationNumber=%s".formatted(existingStationNumber)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(utilitiesService, times(1))
				.getPersonsCoveredByFireStationsInfos(existingStationNumber);
	}

	@Test
	public void getPersonsCoveredByFireStationsInfos_throwsException_test () throws Exception {
		when(utilitiesService.getPersonsCoveredByFireStationsInfos(nonExistentStationNumber))
				.thenThrow(runtimeException);

		mockMvc.perform(get("/firestations_residents?stationNumber=%s".formatted(nonExistentStationNumber)))
				.andExpect(status().isInternalServerError());

		verify(utilitiesService, times(1))
				.getPersonsCoveredByFireStationsInfos(nonExistentStationNumber);
	}

	@Test
	public void getPersonsCoveredByFireStationsInfos_throwsResponseStatusException_test () throws Exception {
		when(utilitiesService.getPersonsCoveredByFireStationsInfos(nonExistentStationNumber))
				.thenThrow(responseStatusExceptionNotFound);

		mockMvc.perform(get("/firestations_residents?stationNumber=%s".formatted(nonExistentStationNumber)))
				.andExpect(status().isNotFound());

		verify(utilitiesService, times(1))
				.getPersonsCoveredByFireStationsInfos(nonExistentStationNumber);
	}

}
