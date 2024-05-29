package com.openclassrooms.safety_net.controller;

import com.openclassrooms.safety_net.model.response.*;
import com.openclassrooms.safety_net.service.PersonService;
import com.openclassrooms.safety_net.service.UtilitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class UtilitiesController {

	@Autowired
	private UtilitiesService utilitiesService;

	@Autowired
	private PersonService personService;

	@GetMapping("/communityEmail")
	public ResponseEntity<Iterable<String>> getPersonsCityMails (@RequestParam("city") final String city) {
		try {
			Iterable<String> personsCityMails = personService.getPersonsEmailByCity(city);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(personsCityMails);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			return ResponseEntity.notFound()
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.build();
		}
	}

	@GetMapping("/personInfo")
	public ResponseEntity<PersonInfosNameEmailAgeAddressMedicals> getPersonInfo (@RequestParam("firstName") final String firstName, @RequestParam("lastName") final String lastName) {
		try {
			PersonInfosNameEmailAgeAddressMedicals personInfo = utilitiesService.getPersonInfo(firstName, lastName);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(personInfo);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			return ResponseEntity.notFound()
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.build();
		}
	}

	@GetMapping("/flood/stations")
	public ResponseEntity<List<AddressInfos>> getHomesResidentsInformationsCoveredByFireStations (@RequestParam("stations") final int stationNumber) {
		try {
			List<AddressInfos> addressInfos = utilitiesService.getHomesResidentsInformationsCoveredByFireStations(stationNumber);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(addressInfos);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			return ResponseEntity.notFound()
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.build();
		}
	}

	@GetMapping("/fire")
	public ResponseEntity<FireInfos> getFireInfos (@RequestParam("label") final String label, @RequestParam("zip") final String zip, @RequestParam("city") final String city) {
		try  {
			FireInfos fireInfos = utilitiesService.getFireInfos(label, zip, city);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(fireInfos);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			return ResponseEntity.notFound()
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.build();
		}
	}

	@GetMapping("/phoneAlert")
	public ResponseEntity<List<String>> getPhoneAlertInfos (@RequestParam("firestation") final int fireStationId) {
		try {
			List<String> phoneAlertInfos = utilitiesService.getPhoneAlertInfos(fireStationId);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(phoneAlertInfos);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			return ResponseEntity.notFound()
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.build();
		}
	}

	@GetMapping("/childAlert")
	public ResponseEntity<List<ChildInfos>> getChildAlertInfos (@RequestParam("label") final String label, @RequestParam("zip") final String zip, @RequestParam("city") final String city) {
		try {
			List<ChildInfos> childAlertInfos = utilitiesService.getChildAlertInfos(label, zip, city);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(childAlertInfos);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			return ResponseEntity.notFound()
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.build();
		}
	}

	@GetMapping("/firestations_residents")
	public ResponseEntity<PersonsCoveredByFireStationsInfos> getPersonsCoveredByFireStationsInfos (@RequestParam("stationNumber") final int stationNumber) {
		try {
			PersonsCoveredByFireStationsInfos personsCoveredByFireStationsInfos = utilitiesService.getPersonsCoveredByFireStationsInfos(stationNumber);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(personsCoveredByFireStationsInfos);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			return ResponseEntity.notFound()
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.build();
		}
	}

}
