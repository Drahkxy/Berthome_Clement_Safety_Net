package com.openclassrooms.safety_net.controller;

import com.openclassrooms.safety_net.model.FireStation;
import com.openclassrooms.safety_net.model.update.FireStationUpdate;
import com.openclassrooms.safety_net.service.FireStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class FireStationController {
	@Autowired
	FireStationService fireStationService;

	@GetMapping("/firestation/{id}")
	public FireStation getFireStationById (@PathVariable("id") final int id) {
		try {
			return fireStationService.getFireStationById(id);
		} catch (ResponseStatusException e) {
			System.out.println(e.getMessage());
			throw e;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while retrieving fire station with %d id.".formatted(id));
		}
	}

	@GetMapping("/firestations")
	public Iterable<FireStation> getFireStations () {
		try {
			return fireStationService.getFireStations();
		} catch (ResponseStatusException e) {
			System.out.println(e.getMessage());
			throw e;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while retrieving fire stations.");
		}
	}

	@GetMapping("/firestations/{stationNumber}")
	public Iterable<FireStation> getFireStationsByStationNumber (@PathVariable("stationNumber") final int stationNumber) {
		try {
			return fireStationService.getFireStationsByStationNumber(stationNumber);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error occurred while retrieving fire stations with %s station number.".formatted(stationNumber));
		}
	}

	@PatchMapping("/firestation/{id}")
	public FireStation updateFireStation (@PathVariable("id") final int id, @RequestBody FireStationUpdate fireStationUpdate) {
		try {
			return fireStationService.updateFireStation(id, fireStationUpdate);
		} catch (ResponseStatusException e) {
			System.out.println(e.getMessage());
			throw e;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while updating fire station.");
		}
	}

	@DeleteMapping("/firestation/{id}")
	public void deleteFireStation (@PathVariable("id") final int id) {
		try {
			fireStationService.deleteFireStation(id);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while deleting fire station with %d id.".formatted(id));
		}
	}

	@PostMapping("/firestation")
	public FireStation addFireStation (@RequestBody FireStation fireStation) {
		try {
			return fireStationService.addFireStation(fireStation);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while creating fire station.");
		}
	}

}
