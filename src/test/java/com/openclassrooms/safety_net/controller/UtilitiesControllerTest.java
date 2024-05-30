package com.openclassrooms.safety_net.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.response.*;
import com.openclassrooms.safety_net.service.PersonService;
import com.openclassrooms.safety_net.service.UtilitiesService;
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
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private UtilitiesService utilitiesService;
	@MockBean
	private PersonService personService;

	@Test
	public void getPersonsCityMails_success_test () throws Exception {
		String city = "Springfield";
		List<String> emails = List.of("john.doe@exemple.com", "doe.jane@exemple.fr");

		when(personService.getPersonsEmailByCity(city)).thenReturn(emails);

		mockMvc.perform(get("/communityEmail?city=%s".formatted(city)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0]").value(emails.get(0)))
				.andExpect(jsonPath("$[1]").value(emails.get(1)));

		verify(personService, times(1)).getPersonsEmailByCity(city);
	}

	@Test
	public void getPersonsCityMails_throwsException_test () throws Exception {
		String city = "Shelbyville";

		when(personService.getPersonsEmailByCity(city)).thenThrow(new RuntimeException("Service exception."));

		mockMvc.perform(get("/communityEmail?city=%s".formatted(city)))
				.andExpect(status().isInternalServerError());

		verify(personService, times(1)).getPersonsEmailByCity(city);
	}

	@Test
	public void getPersonsCityMails_throwsResponseStatusException_test () throws Exception {
		String city = "Shelbyville";

		when(personService.getPersonsEmailByCity(city))
				.thenThrow(
						new ResponseStatusException(HttpStatus.NOT_FOUND, "No person living in %s found.".formatted(city))
				);

		mockMvc.perform(get("/communityEmail?city=%s".formatted(city)))
				.andExpect(status().isNotFound());

		verify(personService, times(1)).getPersonsEmailByCity(city);
	}

	@Test
	public void getPersonInfo_success_test () throws Exception {
		PersonId personId = new PersonId("John", "Doe");
		PersonInfosNameEmailAgeAddressMedicals personInfos = new PersonInfosNameEmailAgeAddressMedicals();

		when(utilitiesService.getPersonInfo(personId.getFirstName(), personId.getLastName()))
				.thenReturn(personInfos);

		mockMvc.perform(get("/personInfo?firstName=%s&lastName=%s".formatted(personId.getFirstName(), personId.getLastName())))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(utilitiesService, times(1))
				.getPersonInfo(personId.getFirstName(), personId.getLastName());
	}

	@Test
	public void getPersonInfo_throwsException_test () throws Exception {
		PersonId personId = new PersonId("Jane", "Doe");

		when(utilitiesService.getPersonInfo(personId.getFirstName(), personId.getLastName()))
				.thenThrow(new RuntimeException("Service exception."));

		mockMvc.perform(get("/personInfo?firstName=%s&lastName=%s".formatted(personId.getFirstName(), personId.getLastName())))
				.andExpect(status().isInternalServerError());

		verify(utilitiesService, times(1))
				.getPersonInfo(personId.getFirstName(), personId.getLastName());
	}

	@Test
	public void getPersonInfo_throwsResponseStatusException_test () throws Exception {
		PersonId personId = new PersonId("Jane", "Doe");

		when(utilitiesService.getPersonInfo(personId.getFirstName(), personId.getLastName()))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No person found."));

		mockMvc.perform(get("/personInfo?firstName=%s&lastName=%s".formatted(personId.getFirstName(), personId.getLastName())))
				.andExpect(status().isNotFound());

		verify(utilitiesService, times(1))
				.getPersonInfo(personId.getFirstName(), personId.getLastName());
	}

	@Test
	public void getHomesResidentsInformationsCoveredByFireStations_success_test () throws Exception {
		int stationNumber = 1;
		List<AddressInfos> addressInfos = List.of(new AddressInfos(), new AddressInfos());

		when(utilitiesService.getHomesResidentsInformationsCoveredByFireStations(stationNumber))
				.thenReturn(addressInfos);

		mockMvc.perform(get("/flood/stations?stations=%d".formatted(stationNumber)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(utilitiesService, times(1))
				.getHomesResidentsInformationsCoveredByFireStations(stationNumber);
	}

	@Test
	public void getHomesResidentsInformationsCoveredByFireStations_throwsException_test () throws Exception {
		int stationNumber = 1;

		when(utilitiesService.getHomesResidentsInformationsCoveredByFireStations(stationNumber))
				.thenThrow(new RuntimeException("Service exception."));

		mockMvc.perform(get("/flood/stations?stations=%d".formatted(stationNumber)))
				.andExpect(status().isInternalServerError());

		verify(utilitiesService, times(1))
				.getHomesResidentsInformationsCoveredByFireStations(stationNumber);
	}

	@Test
	public void getHomesResidentsInformationsCoveredByFireStations_throwsResponseStatusException_test () throws Exception {
		int stationNumber = 1;

		when(utilitiesService.getHomesResidentsInformationsCoveredByFireStations(stationNumber))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No fire station found."));

		mockMvc.perform(get("/flood/stations?stations=%d".formatted(stationNumber)))
				.andExpect(status().isNotFound());

		verify(utilitiesService, times(1))
				.getHomesResidentsInformationsCoveredByFireStations(stationNumber);
	}

	@Test
	public void getFireInfos_success_test () throws Exception {
		Address address = new Address("123 Main St", "12345", "Springfield");
		FireInfos fireInfos = new FireInfos();

		when(utilitiesService.getFireInfos(address.getLabel(), address.getZip(), address.getCity()))
				.thenReturn(fireInfos);

		mockMvc.perform(get("/fire?label=%s&zip=%s&city=%s".formatted(address.getLabel(), address.getZip(), address.getCity())))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(utilitiesService, times(1))
				.getFireInfos(address.getLabel(), address.getZip(), address.getCity());
	}

	@Test
	public void getFireInfos_throwsException_test () throws Exception {
		Address address = new Address("459 Elm St", "67890", "Shelbyville");

		when(utilitiesService.getFireInfos(address.getLabel(), address.getZip(), address.getCity()))
				.thenThrow(new RuntimeException("Service exception."));

		mockMvc.perform(get("/fire?label=%s&zip=%s&city=%s".formatted(address.getLabel(), address.getZip(), address.getCity())))
				.andExpect(status().isInternalServerError());

		verify(utilitiesService, times(1))
				.getFireInfos(address.getLabel(), address.getZip(), address.getCity());
	}

	@Test
	public void getFireInfos_throwsResponseStatusException_test () throws Exception {
		Address address = new Address("459 Elm St", "67890", "Shelbyville");

		when(utilitiesService.getFireInfos(address.getLabel(), address.getZip(), address.getCity()))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found."));

		mockMvc.perform(get("/fire?label=%s&zip=%s&city=%s".formatted(address.getLabel(), address.getZip(), address.getCity())))
				.andExpect(status().isNotFound());

		verify(utilitiesService, times(1))
				.getFireInfos(address.getLabel(), address.getZip(), address.getCity());
	}

	@Test
	public void getPhoneAlertInfos_success_test () throws Exception {
		int stationNumber = 1;
		List<String> phones = List.of("0123456789", "9876543210");

		when(utilitiesService.getPhoneAlertInfos(stationNumber)).thenReturn(phones);

		mockMvc.perform(get("/phoneAlert?firestation=%s".formatted(stationNumber)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(utilitiesService, times(1)).getPhoneAlertInfos(stationNumber);
	}

	@Test
	public void getPhoneAlertInfos_throwsException_test () throws Exception {
		int stationNumber = 1;

		when(utilitiesService.getPhoneAlertInfos(stationNumber))
				.thenThrow(new RuntimeException("Service exception."));

		mockMvc.perform(get("/phoneAlert?firestation=%s".formatted(stationNumber)))
				.andExpect(status().isInternalServerError());

		verify(utilitiesService, times(1)).getPhoneAlertInfos(stationNumber);
	}

	@Test
	public void getPhoneAlertInfos_throwsResponseStatusException_test () throws Exception {
		int stationNumber = 1;

		when(utilitiesService.getPhoneAlertInfos(stationNumber))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No fire station found."));

		mockMvc.perform(get("/phoneAlert?firestation=%s".formatted(stationNumber)))
				.andExpect(status().isNotFound());

		verify(utilitiesService, times(1)).getPhoneAlertInfos(stationNumber);
	}
	@Test
	public void getChildAlertInfos_success_test () throws Exception {
		Address address = new Address("123 Main St", "12345", "Springfield");
		List<ChildInfos> childInfos = List.of(new ChildInfos(), new ChildInfos());

		when(utilitiesService.getChildAlertInfos(address.getLabel(), address.getZip(), address.getCity()))
				.thenReturn(childInfos);

		mockMvc.perform(get("/childAlert?label=%s&zip=%s&city=%s".formatted(address.getLabel(), address.getZip(), address.getCity())))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(utilitiesService, times(1))
				.getChildAlertInfos(address.getLabel(), address.getZip(), address.getCity());
	}

	@Test
	public void getChildAlertInfos_throwsException_test () throws Exception {
		Address address = new Address("459 Elm St", "67890", "Shelbyville");

		when(utilitiesService.getChildAlertInfos(address.getLabel(), address.getZip(), address.getCity()))
				.thenThrow(new RuntimeException("Service exception."));

		mockMvc.perform(get("/childAlert?label=%s&zip=%s&city=%s".formatted(address.getLabel(), address.getZip(), address.getCity())))
				.andExpect(status().isInternalServerError());

		verify(utilitiesService, times(1))
				.getChildAlertInfos(address.getLabel(), address.getZip(), address.getCity());
	}

	@Test
	public void getChildAlertInfos_throwsResponseStatusException_test () throws Exception {
		Address address = new Address("459 Elm St", "67890", "Shelbyville");

		when(utilitiesService.getChildAlertInfos(address.getLabel(), address.getZip(), address.getCity()))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found."));

		mockMvc.perform(get("/childAlert?label=%s&zip=%s&city=%s".formatted(address.getLabel(), address.getZip(), address.getCity())))
				.andExpect(status().isNotFound());

		verify(utilitiesService, times(1))
				.getChildAlertInfos(address.getLabel(), address.getZip(), address.getCity());
	}

	@Test
	public void getPersonsCoveredByFireStationsInfos_success_test () throws Exception {
		int stationNumber = 1;
		PersonsCoveredByFireStationsInfos personsCoveredByFireStationsInfos = new PersonsCoveredByFireStationsInfos();

		when(utilitiesService.getPersonsCoveredByFireStationsInfos(stationNumber))
				.thenReturn(personsCoveredByFireStationsInfos);

		mockMvc.perform(get("/firestations_residents?stationNumber=%s".formatted(stationNumber)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(utilitiesService, times(1)).getPersonsCoveredByFireStationsInfos(stationNumber);
	}

	@Test
	public void getPersonsCoveredByFireStationsInfos_throwsException_test () throws Exception {
		int stationNumber = 1;

		when(utilitiesService.getPersonsCoveredByFireStationsInfos(stationNumber))
				.thenThrow(new RuntimeException("Service exception."));

		mockMvc.perform(get("/firestations_residents?stationNumber=%s".formatted(stationNumber)))
				.andExpect(status().isInternalServerError());

		verify(utilitiesService, times(1)).getPersonsCoveredByFireStationsInfos(stationNumber);
	}

	@Test
	public void getPersonsCoveredByFireStationsInfos_throwsResponseStatusException_test () throws Exception {
		int stationNumber = 1;

		when(utilitiesService.getPersonsCoveredByFireStationsInfos(stationNumber))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No fire station found."));

		mockMvc.perform(get("/firestations_residents?stationNumber=%s".formatted(stationNumber)))
				.andExpect(status().isNotFound());

		verify(utilitiesService, times(1)).getPersonsCoveredByFireStationsInfos(stationNumber);
	}

}
