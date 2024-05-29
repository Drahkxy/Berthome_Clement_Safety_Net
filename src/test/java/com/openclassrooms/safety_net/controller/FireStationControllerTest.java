package com.openclassrooms.safety_net.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.model.FireStation;
import com.openclassrooms.safety_net.model.update.FireStationUpdate;
import com.openclassrooms.safety_net.service.FireStationService;
import org.junit.jupiter.api.BeforeEach;
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
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private FireStationService fireStationService;

	private String fireStationJson;
	private FireStation fireStation;
	private FireStation otherFireStation;


	@BeforeEach
	public void setUp () throws JsonProcessingException {
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
		when(fireStationService.getFireStations()).thenThrow(new RuntimeException("Service exception"));

		mockMvc.perform(get("/firestations"))
				.andExpect(status().isInternalServerError());

		verify(fireStationService, times(1)).getFireStations();
	}

	@Test
	public void getFireStations_throwsResponseStatusException_test () throws Exception {
		when(fireStationService.getFireStations())
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No fire station found."));

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
		when(fireStationService.getFireStationById(fireStation.getId()))
				.thenThrow(new RuntimeException("Service exception"));

		mockMvc.perform(get("/firestation?id=%d".formatted(fireStation.getId())))
				.andExpect(status().isInternalServerError());

		verify(fireStationService, times(1)).getFireStationById(fireStation.getId());
	}

	@Test
	public void getFireStationById_throwsResponseStatusException_test () throws Exception {
		when(fireStationService.getFireStationById(fireStation.getId()))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No fire station found"));

		mockMvc.perform(get("/firestation?id=%d".formatted(fireStation.getId())))
				.andExpect(status().isNotFound());

		verify(fireStationService, times(1)).getFireStationById(fireStation.getId());
	}

	@Test
	public void getFireStationByStationNumber_success_test () throws Exception {
		when(fireStationService.getFireStationsByStationNumber(fireStation.getStation())).thenReturn(List.of(fireStation));

		mockMvc.perform(get("/firestations_by_number?stationNumber=%d".formatted(fireStation.getStation())))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id").value(fireStation.getId()))
				.andExpect(jsonPath("$[0].station").value(fireStation.getStation()))
				.andExpect(jsonPath("$[0].address").value(fireStation.getAddress()));

		verify(fireStationService, times(1)).getFireStationsByStationNumber(fireStation.getStation());
	}

	@Test
	public void getFireStationByStationNumber_throwsException_test () throws Exception {
		when(fireStationService.getFireStationsByStationNumber(fireStation.getStation()))
				.thenThrow(new RuntimeException("Service exception"));

		mockMvc.perform(get("/firestations_by_number?stationNumber=%d".formatted(fireStation.getStation())))
				.andExpect(status().isInternalServerError());

		verify(fireStationService, times(1)).getFireStationsByStationNumber(fireStation.getStation());
	}

	@Test
	public void getFireStationByStationNumber_throwsResponseStatusException_test () throws Exception {
		when(fireStationService.getFireStationsByStationNumber(fireStation.getStation()))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No fire station found"));

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
		doThrow(new RuntimeException("Service exception"))
				.when(fireStationService).deleteFireStation(fireStation.getId());

		mockMvc.perform(delete("/firestation?id=%d".formatted(fireStation.getId())))
				.andExpect(status().isInternalServerError());

		verify(fireStationService, times(1)).deleteFireStation(fireStation.getId());
	}

	@Test
	public void deleteFireStation_throwsResponseStatusException_test () throws Exception {
		doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No fire station found and can't be deleted."))
				.when(fireStationService).deleteFireStation(fireStation.getId());

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
		when(fireStationService.addFireStation(fireStation)).thenThrow(new RuntimeException("Service exception"));

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
		when(fireStationService.addFireStation(fireStation))
				.thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fire station already exist."));

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

		when(fireStationService.updateFireStation(fireStation.getId(), fsUpdate))
				.thenThrow(new RuntimeException("Service exception"));

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
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Fire station not found and can't be updated."));

		mockMvc.perform(
						patch("/firestation?id=%d".formatted(fireStation.getId()))
								.contentType(MediaType.APPLICATION_JSON)
								.content(fsUpdateJson)
				)
				.andExpect(status().isNotFound());

		verify(fireStationService, times(1)).updateFireStation(fireStation.getId(), fsUpdate);
	}

}
