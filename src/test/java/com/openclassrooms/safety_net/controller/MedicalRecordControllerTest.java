package com.openclassrooms.safety_net.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safety_net.model.Allergy;
import com.openclassrooms.safety_net.model.MedicalRecord;
import com.openclassrooms.safety_net.model.Medication;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import com.openclassrooms.safety_net.model.update.MedicalRecordUpdate;
import com.openclassrooms.safety_net.service.MedicalRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MedicalRecordController.class)
public class MedicalRecordControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private MedicalRecordService medicalRecordService;

	private PersonId existingMedicalRecordId;
	private PersonId nonExistentMedicalRecordId;
	private MedicalRecord medicalRecord;
	private String medicalRecordJson;
	private MedicalRecord otherMedicalRecord;

	@BeforeEach
	public void setUp () throws JsonProcessingException {
		existingMedicalRecordId = new PersonId("John", "Doe");
		nonExistentMedicalRecordId = new PersonId("Jane", "Doe");

		MedicalRecord mr = new MedicalRecord(
				existingMedicalRecordId.getFirstName(),
				existingMedicalRecordId.getLastName(),
				LocalDate.of(	1996, 3, 9)
		);

		MedicalRecord otherMr = new MedicalRecord(
				nonExistentMedicalRecordId.getFirstName(),
				nonExistentMedicalRecordId.getLastName(),
				LocalDate.of(1997, 6, 23)
		);

		Allergy allergy = new Allergy("peanut");
		Medication medication = new Medication("aznol", 100);

		mr.addAllergy(allergy);
		mr.addMedication(medication);

		otherMr.addAllergy(allergy);
		otherMr.addMedication(medication);

		medicalRecordJson = objectMapper.writeValueAsString(mr);
		medicalRecord = objectMapper.readValue(medicalRecordJson, MedicalRecord.class);

		String otherMedicalRecordJson = objectMapper.writeValueAsString(otherMr);
		otherMedicalRecord = objectMapper.readValue(otherMedicalRecordJson, MedicalRecord.class);
	}

	@Test
	public void getMedicalRecords_success_test () throws Exception {
		List<MedicalRecord> medicalRecords = List.of(medicalRecord, otherMedicalRecord);

		when(medicalRecordService.getMedicalRecords()).thenReturn(medicalRecords);

		mockMvc.perform(get("/medical_records"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].firstName").value(medicalRecord.getFirstName()))
				.andExpect(jsonPath("$[0].lastName").value(medicalRecord.getLastName()))
				.andExpect(jsonPath("$[0].birthday")
						.value(medicalRecord.getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
				.andExpect(jsonPath("$[1].firstName").value(otherMedicalRecord.getFirstName()))
				.andExpect(jsonPath("$[1].lastName").value(otherMedicalRecord.getLastName()))
				.andExpect(jsonPath("$[1].birthday")
						.value(otherMedicalRecord.getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

		verify(medicalRecordService, times(1)).getMedicalRecords();
	}

	@Test
	public void getMedicalRecords_throwsException_test () throws Exception {
		when(medicalRecordService.getMedicalRecords()).thenThrow(new RuntimeException("Service exception"));

		mockMvc.perform(get("/medical_records"))
				.andExpect(status().isInternalServerError());

		verify(medicalRecordService, times(1)).getMedicalRecords();
	}

	@Test
	public void getMedicalRecords_throwsResponseStatusException_test () throws Exception {
		when(medicalRecordService.getMedicalRecords())
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found."));

		mockMvc.perform(get("/medical_records"))
				.andExpect(status().isNotFound());

		verify(medicalRecordService, times(1)).getMedicalRecords();
	}

	@Test
	public void getMedicalRecord_success_test () throws Exception {
		when(medicalRecordService.getMedicalRecordById(existingMedicalRecordId)).thenReturn(medicalRecord);

		mockMvc.perform(get("/medical_record?first_name=%s&last_name=%s"
						.formatted(existingMedicalRecordId.getFirstName(), existingMedicalRecordId.getLastName())))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("firstName").value(medicalRecord.getFirstName()))
				.andExpect(jsonPath("lastName").value(medicalRecord.getLastName()))
				.andExpect(jsonPath("birthday")
						.value(medicalRecord.getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

		verify(medicalRecordService, times(1)).getMedicalRecordById(existingMedicalRecordId);
	}

	@Test
	public void getMedicalRecord_throwsException_test () throws Exception {
		when(medicalRecordService.getMedicalRecordById(nonExistentMedicalRecordId))
				.thenThrow(new RuntimeException("Service exception"));

		mockMvc.perform(get("/medical_record?first_name=%s&last_name=%s"
						.formatted(nonExistentMedicalRecordId.getFirstName(), nonExistentMedicalRecordId.getLastName())))
				.andExpect(status().isInternalServerError());

		verify(medicalRecordService, times(1)).getMedicalRecordById(nonExistentMedicalRecordId);
	}

	@Test
	public void getMedicalRecord_throwsResponseStatusException_test () throws Exception {
		when(medicalRecordService.getMedicalRecordById(nonExistentMedicalRecordId))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found."));

		mockMvc.perform(get("/medical_record?first_name=%s&last_name=%s"
						.formatted(nonExistentMedicalRecordId.getFirstName(), nonExistentMedicalRecordId.getLastName())))
				.andExpect(status().isNotFound());

		verify(medicalRecordService, times(1)).getMedicalRecordById(nonExistentMedicalRecordId);
	}

	@Test
	public void deleteMedicalRecord_success_test () throws Exception {
		doNothing().when(medicalRecordService).deleteMedicalRecord(existingMedicalRecordId);

		mockMvc.perform(delete("/medical_record?first_name=%s&last_name=%s"
						.formatted(existingMedicalRecordId.getFirstName(), existingMedicalRecordId.getLastName())))
				.andExpect(status().isOk());

		verify(medicalRecordService, times(1)).deleteMedicalRecord(existingMedicalRecordId);
	}

	@Test
	public void deleteMedicalRecord_throwsException_test () throws Exception {
		doThrow(new RuntimeException("Service exception."))
				.when(medicalRecordService)
				.deleteMedicalRecord(nonExistentMedicalRecordId);

		mockMvc.perform(delete("/medical_record?first_name=%s&last_name=%s"
						.formatted(nonExistentMedicalRecordId.getFirstName(), nonExistentMedicalRecordId.getLastName())))
				.andExpect(status().isInternalServerError());

		verify(medicalRecordService, times(1)).deleteMedicalRecord(nonExistentMedicalRecordId);
	}

	@Test
	public void deleteMedicalRecord_throwsResponseStatusException_test () throws Exception {
		doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found and can't be deleted."))
				.when(medicalRecordService)
				.deleteMedicalRecord(nonExistentMedicalRecordId);

		mockMvc.perform(delete("/medical_record?first_name=%s&last_name=%s"
						.formatted(nonExistentMedicalRecordId.getFirstName(), nonExistentMedicalRecordId.getLastName())))
				.andExpect(status().isNotFound());

		verify(medicalRecordService, times(1)).deleteMedicalRecord(nonExistentMedicalRecordId);
	}

	@Test
	public void addMedicalRecord_success_test () throws Exception {
		when(medicalRecordService.addMedicalRecord(medicalRecord)).thenReturn(medicalRecord);

		mockMvc.perform(
				post("/medical_record")
						.contentType(MediaType.APPLICATION_JSON)
						.content(medicalRecordJson)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("firstName").value(medicalRecord.getFirstName()))
				.andExpect(jsonPath("lastName").value(medicalRecord.getLastName()))
				.andExpect(jsonPath("birthday")
						.value(medicalRecord.getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

		verify(medicalRecordService, times(1)).addMedicalRecord(medicalRecord);
	}

	@Test
	public void addMedicalRecord_throwsException_test () throws Exception {
		when(medicalRecordService.addMedicalRecord(medicalRecord))
				.thenThrow(new RuntimeException("Service exception."));

		mockMvc.perform(
						post("/medical_record")
								.contentType(MediaType.APPLICATION_JSON)
								.content(medicalRecordJson)
				)
				.andExpect(status().isInternalServerError());

		verify(medicalRecordService, times(1)).addMedicalRecord(medicalRecord);
	}

	@Test
	public void addMedicalRecord_throwsResponseStatusException_test () throws Exception {
		when(medicalRecordService.addMedicalRecord(medicalRecord))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found and can't be deleted."));

		mockMvc.perform(
						post("/medical_record")
								.contentType(MediaType.APPLICATION_JSON)
								.content(medicalRecordJson)
				)
				.andExpect(status().isBadRequest());

		verify(medicalRecordService, times(1)).addMedicalRecord(medicalRecord);
	}

	@Test
	public void updateMedicalRecord_success_test () throws Exception {
		MedicalRecordUpdate medicalRecordUpdate = new MedicalRecordUpdate();
		String medicalRecordUpdateJson = objectMapper.writeValueAsString(medicalRecordUpdate);
		medicalRecordUpdate = objectMapper.readValue(medicalRecordUpdateJson, MedicalRecordUpdate.class);

		when(medicalRecordService.updateMedicalRecord(existingMedicalRecordId, medicalRecordUpdate))
				.thenReturn(medicalRecord);

		mockMvc.perform(
						patch("/medical_record?first_name=%s&last_name=%s".formatted(
								existingMedicalRecordId.getFirstName(),
								existingMedicalRecordId.getLastName()
						))
								.contentType(MediaType.APPLICATION_JSON)
								.content(medicalRecordUpdateJson)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("firstName").value(medicalRecord.getFirstName()))
				.andExpect(jsonPath("lastName").value(medicalRecord.getLastName()))
				.andExpect(jsonPath("birthday")
						.value(medicalRecord.getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

		verify(medicalRecordService, times(1))
				.updateMedicalRecord(existingMedicalRecordId, medicalRecordUpdate);
	}

	@Test
	public void updateMedicalRecord_throwsException_test () throws Exception {
		MedicalRecordUpdate medicalRecordUpdate = new MedicalRecordUpdate();
		String medicalRecordUpdateJson = objectMapper.writeValueAsString(medicalRecordUpdate);
		medicalRecordUpdate = objectMapper.readValue(medicalRecordUpdateJson, MedicalRecordUpdate.class);

		when(medicalRecordService.updateMedicalRecord(nonExistentMedicalRecordId, medicalRecordUpdate))
				.thenThrow(new RuntimeException("Service exception."));

		mockMvc.perform(
						patch("/medical_record?first_name=%s&last_name=%s".formatted(
								nonExistentMedicalRecordId.getFirstName(),
								nonExistentMedicalRecordId.getLastName()
						))
								.contentType(MediaType.APPLICATION_JSON)
								.content(medicalRecordUpdateJson)
				)
				.andExpect(status().isInternalServerError());

		verify(medicalRecordService, times(1))
				.updateMedicalRecord(nonExistentMedicalRecordId, medicalRecordUpdate);
	}

	@Test
	public void updateMedicalRecord_throwsResponseStatusException_test () throws Exception {
		MedicalRecordUpdate medicalRecordUpdate = new MedicalRecordUpdate();
		String medicalRecordUpdateJson = objectMapper.writeValueAsString(medicalRecordUpdate);
		medicalRecordUpdate = objectMapper.readValue(medicalRecordUpdateJson, MedicalRecordUpdate.class);

		when(medicalRecordService.updateMedicalRecord(nonExistentMedicalRecordId, medicalRecordUpdate))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found and can't be deleted."));

		mockMvc.perform(
						patch("/medical_record?first_name=%s&last_name=%s".formatted(
								nonExistentMedicalRecordId.getFirstName(),
								nonExistentMedicalRecordId.getLastName()
						))
								.contentType(MediaType.APPLICATION_JSON)
								.content(medicalRecordUpdateJson)
				)
				.andExpect(status().isNotFound());

		verify(medicalRecordService, times(1))
				.updateMedicalRecord(nonExistentMedicalRecordId, medicalRecordUpdate);
	}

}
