package com.openclassrooms.safety_net.controller;

import com.openclassrooms.safety_net.model.response.PersonInfo;
import com.openclassrooms.safety_net.service.PersonService;
import com.openclassrooms.safety_net.service.UtilitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error occurred while retrieving persons by city.");
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
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error occurred while retrieving %s %s information's.".formatted(firstName, lastName));
		}
	}

}
