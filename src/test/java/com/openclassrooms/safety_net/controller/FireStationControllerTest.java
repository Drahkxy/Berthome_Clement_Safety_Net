package com.openclassrooms.safety_net.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.model.FireStation;
import com.openclassrooms.safety_net.model.update.FireStationUpdate;
import com.openclassrooms.safety_net.service.FireStationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FireStationController.class)
public class FireStationControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private FireStationService fireStationService;
	@MockBean
	private DbSetup dbSetup;

	private static ObjectMapper objectMapper;
	private static String fireStationJson;
	private static FireStation fireStation;
	private static FireStation otherFireStation;
	private static RuntimeException runtimeException;
	private static ResponseStatusException responseStatusExceptionNotFound;

	@BeforeAll
	public static void setUp () throws JsonProcessingException {
		objectMapper = new ObjectMapper();

		Address address = new Address("123 Main St", "12345", "Springfield");
		address.setId(1);

		FireStation f = new FireStation(1);
		f.setId(1);

		FireStation otherF = new FireStation(2);
		otherF.setId(2);

		address.addFireStation(f);
		address.addFireStation(otherF);

		fireStationJson = objectMapper.writeValueAsString(f);
		fireStation = objectMapper.readValue(fireStationJson, FireStation.class);

		String otherFireStationJson = objectMapper.writeValueAsString(otherF);
		otherFireStation = objectMapper.readValue(otherFireStationJson, FireStation.class);

		runtimeException = new RuntimeException("Service exception.");
		responseStatusExceptionNotFound = new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found.");
	}


	@Test
	public void getFireStations_success_test () throws Exception {
		List<FireStation> fireStations = List.of(fireStation, otherFireStation);

		when(fireStationService.getFireStations()).thenReturn(fireStations);

		mockMvc.perform(get("/firestations"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id").value(fireStation.getId()))
				.andExpect(jsonPath("$[0].station").value(fireStation.getStation()))
				.andExpect(jsonPath("$[0].address").value(fireStation.getAddress()))
				.andExpect(jsonPath("$[1].id").value(otherFireStation.getId()))
				.andExpect(jsonPath("$[1].station").value(otherFireStation.getStation()))
				.andExpect(jsonPath("$[1].address").value(otherFireStation.getAddress()));

		verify(fireStationService, times(1)).getFireStations();
	}

	@Test
	public void getFireStations_throwsException_test () throws Exception {
		when(fireStationService.getFireStations()).thenThrow(runtimeException);

		mockMvc.perform(get("/firestations"))
				.andExpect(status().isInternalServerError());

		verify(fireStationService, times(1)).getFireStations();
	}

	@Test
	public void getFireStations_throwsResponseStatusException_test () throws Exception {
		when(fireStationService.getFireStations()).thenThrow(responseStatusExceptionNotFound);

		mockMvc.perform(get("/firestations"))
				.andExpect(status().isNotFound());

		verify(fireStationService, times(1)).getFireStations();
	}

	@Test
	public void getFireStationById_success_test () throws Exception {
		when(fireStationService.getFireStationById(fireStation.getId())).thenReturn(fireStation);

		mockMvc.perform(get("/firestation?id=%d".formatted(fireStation.getId())))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("id").value(fireStation.getId()))
				.andExpect(jsonPath("station").value(fireStation.getStation()))
				.andExpect(jsonPath("address").value(fireStation.getAddress()));

		verify(fireStationService, times(1)).getFireStationById(fireStation.getId());
	}

	@Test
	public void getFireStationById_throwsException_test () throws Exception {
		when(fireStationService.getFireStationById(fireStation.getId())).thenThrow(runtimeException);

		mockMvc.perform(get("/firestation?id=%d".formatted(fireStation.getId())))
				.andExpect(status().isInternalServerError());

		verify(fireStationService, times(1)).getFireStationById(fireStation.getId());
	}

	@Test
	public void getFireStationById_throwsResponseStatusException_test () throws Exception {
		when(fireStationService.getFireStationById(fireStation.getId())).thenThrow(responseStatusExceptionNotFound);

		mockMvc.perform(get("/firestation?id=%d".formatted(fireStation.getId())))
				.andExpect(status().isNotFound());

		verify(fireStationService, times(1)).getFireStationById(fireStation.getId());
	}

	@Test
	public void getFireStationByStationNumber_success_test () throws Exception {
		when(fireStationService.getFireStationsByStationNumber(fireStation.getStation()))
				.thenReturn(List.of(fireStation));

		mockMvc.perform(get("/firestations_by_number?stationNumber=%d".formatted(fireStation.getStation())))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id").value(fireStation.getId()))
				.andExpect(jsonPath("$[0].station").value(fireStation.getStation()))
				.andExpect(jsonPath("$[0].address").value(fireStation.getAddress()));

		verify(fireStationService, times(1))
				.getFireStationsByStationNumber(fireStation.getStation());
	}

	@Test
	public void getFireStationByStationNumber_throwsException_test () throws Exception {
		when(fireStationService.getFireStationsByStationNumber(fireStation.getStation()))
				.thenThrow(runtimeException);

		mockMvc.perform(get("/firestations_by_number?stationNumber=%d".formatted(fireStation.getStation())))
				.andExpect(status().isInternalServerError());

		verify(fireStationService, times(1))
				.getFireStationsByStationNumber(fireStation.getStation());
	}

	@Test
	public void getFireStationByStationNumber_throwsResponseStatusException_test () throws Exception {
		when(fireStationService.getFireStationsByStationNumber(fireStation.getStation()))
				.thenThrow(responseStatusExceptionNotFound);

		mockMvc.perform(get("/firestations_by_number?stationNumber=%d".formatted(fireStation.getStation())))
				.andExpect(status().isNotFound());

		verify(fireStationService, times(1)).getFireStationsByStationNumber(fireStation.getStation());
	}

	@Test
	public void deleteFireStation_success_test () throws Exception {
		doNothing().when(fireStationService).deleteFireStation(fireStation.getId());

		mockMvc.perform(delete("/firestation?id=%d".formatted(fireStation.getId())))
				.andExpect(status().isOk());

		verify(fireStationService, times(1)).deleteFireStation(fireStation.getId());
	}

	@Test
	public void deleteFireStation_throwsException_test () throws Exception {
		doThrow(runtimeException).when(fireStationService).deleteFireStation(fireStation.getId());

		mockMvc.perform(delete("/firestation?id=%d".formatted(fireStation.getId())))
				.andExpect(status().isInternalServerError());

		verify(fireStationService, times(1)).deleteFireStation(fireStation.getId());
	}

	@Test
	public void deleteFireStation_throwsResponseStatusException_test () throws Exception {
		doThrow(responseStatusExceptionNotFound).when(fireStationService).deleteFireStation(fireStation.getId());

		mockMvc.perform(delete("/firestation?id=%d".formatted(fireStation.getId())))
				.andExpect(status().isNotFound());

		verify(fireStationService, times(1)).deleteFireStation(fireStation.getId());
	}

	@Test
	public void addFireStation_success_test () throws Exception {
		when(fireStationService.addFireStation(fireStation)).thenReturn(fireStation);

		mockMvc.perform(
				post("/firestation")
						.contentType(MediaType.APPLICATION_JSON)
						.content(fireStationJson)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("id").value(fireStation.getId()))
				.andExpect(jsonPath("station").value(fireStation.getStation()))
				.andExpect(jsonPath("address").value(fireStation.getAddress()));

		verify(fireStationService, times(1)).addFireStation(fireStation);
	}

	@Test
	public void addFireStation_throwsException_test () throws Exception {
		when(fireStationService.addFireStation(fireStation)).thenThrow(runtimeException);

		mockMvc.perform(
						post("/firestation")
								.contentType(MediaType.APPLICATION_JSON)
								.content(fireStationJson)
				)
				.andExpect(status().isInternalServerError());

		verify(fireStationService, times(1)).addFireStation(fireStation);
	}

	@Test
	public void addFireStation_throwsResponseStatusException_test () throws Exception {
		when(fireStationService.addFireStation(fireStation)).thenThrow(responseStatusExceptionNotFound);

		mockMvc.perform(
						post("/firestation")
								.contentType(MediaType.APPLICATION_JSON)
								.content(fireStationJson)
				)
				.andExpect(status().isBadRequest());

		verify(fireStationService, times(1)).addFireStation(fireStation);
	}

	@Test
	public void updateFireStation_success_test () throws Exception {
		FireStationUpdate fsUpdate = new FireStationUpdate();
		String fsUpdateJson = objectMapper.writeValueAsString(fsUpdate);
		fsUpdate = objectMapper.readValue(fsUpdateJson, FireStationUpdate.class);

		when(fireStationService.updateFireStation(fireStation.getId(), fsUpdate)).thenReturn(fireStation);

		mockMvc.perform(
						patch("/firestation?id=%d".formatted(fireStation.getId()))
								.contentType(MediaType.APPLICATION_JSON)
								.content(fsUpdateJson)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("id").value(fireStation.getId()))
				.andExpect(jsonPath("station").value(fireStation.getStation()))
				.andExpect(jsonPath("address").value(fireStation.getAddress()));

		verify(fireStationService, times(1)).updateFireStation(fireStation.getId(), fsUpdate);
	}

	@Test
	public void updateFireStation_throwsException_test () throws Exception {
		FireStationUpdate fsUpdate = new FireStationUpdate();
		String fsUpdateJson = objectMapper.writeValueAsString(fsUpdate);
		fsUpdate = objectMapper.readValue(fsUpdateJson, FireStationUpdate.class);

		when(fireStationService.updateFireStation(fireStation.getId(), fsUpdate)).thenThrow(runtimeException);

		mockMvc.perform(
						patch("/firestation?id=%d".formatted(fireStation.getId()))
								.contentType(MediaType.APPLICATION_JSON)
								.content(fsUpdateJson)
				)
				.andExpect(status().isInternalServerError());

		verify(fireStationService, times(1)).updateFireStation(fireStation.getId(), fsUpdate);
	}

	@Test
	public void updateFireStation_throwsResponseStatusException_test () throws Exception {
		FireStationUpdate fsUpdate = new FireStationUpdate();
		String fsUpdateJson = objectMapper.writeValueAsString(fsUpdate);
		fsUpdate = objectMapper.readValue(fsUpdateJson, FireStationUpdate.class);

		when(fireStationService.updateFireStation(fireStation.getId(), fsUpdate))
				.thenThrow(responseStatusExceptionNotFound);

		mockMvc.perform(
						patch("/firestation?id=%d".formatted(fireStation.getId()))
								.contentType(MediaType.APPLICATION_JSON)
								.content(fsUpdateJson)
				)
				.andExpect(status().isNotFound());

		verify(fireStationService, times(1)).updateFireStation(fireStation.getId(), fsUpdate);
	}

}
