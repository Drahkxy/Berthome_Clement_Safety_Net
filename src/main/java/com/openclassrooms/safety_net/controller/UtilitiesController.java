package com.openclassrooms.safety_net.controller;

import com.openclassrooms.safety_net.model.response.AddressInfo;
import com.openclassrooms.safety_net.model.response.PersonInfo;
import com.openclassrooms.safety_net.service.PersonService;
import com.openclassrooms.safety_net.service.UtilitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
	public Iterable<String> getPersonsCityMails (@RequestParam("city") final String city) {
		try {
			return personService.getPersonsEmailByCity(city);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while retrieving persons by city.");
		}
	}

	@GetMapping("/personInfo")
	public PersonInfo getPersonInfo (@RequestParam("firstName") final String firstName, @RequestParam("lastName") final String lastName) {
		try {
			return utilitiesService.getPersonInfo(firstName, lastName);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while retrieving %s %s information's.".formatted(firstName, lastName));
		}
	}

	@GetMapping("/flood/stations")
	public List<AddressInfo> getPersonsCoveredByFireStationsInfos (@RequestParam("stations") final int stationNumber) {
		try {
			return utilitiesService.getPersonsCoveredByFireStationsInfos(stationNumber);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while retrieving addresses information's covered by fire stations with %d station number.".formatted(stationNumber));
		}
	}

}
