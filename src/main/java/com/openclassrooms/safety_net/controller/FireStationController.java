package com.openclassrooms.safety_net.controller;

import com.openclassrooms.safety_net.model.FireStation;
import com.openclassrooms.safety_net.model.update.FireStationUpdate;
import com.openclassrooms.safety_net.service.FireStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class FireStationController {
	@Autowired
	FireStationService fireStationService;

	@GetMapping("/firestation")
	public ResponseEntity<FireStation> getFireStationById (@RequestParam("id") final int id) {
		try {
			FireStation fireStation = fireStationService.getFireStationById(id);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(fireStation);
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

	@GetMapping("/firestations")
	public ResponseEntity<Iterable<FireStation>> getFireStations () {
		try {
			Iterable<FireStation> fireStations = fireStationService.getFireStations();
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(fireStations);
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

	@GetMapping("/firestations_by_number")
	public ResponseEntity<Iterable<FireStation>> getFireStationsByStationNumber (@RequestParam("stationNumber") final int stationNumber) {
		try {
			Iterable<FireStation> fireStations = fireStationService.getFireStationsByStationNumber(stationNumber);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(fireStations);
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

	@PatchMapping("/firestation")
	public ResponseEntity<FireStation> updateFireStation (@RequestParam("id") final int id, @RequestBody FireStationUpdate fireStationUpdate) {
		try {
			FireStation fireStationUpdated = fireStationService.updateFireStation(id, fireStationUpdate);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(fireStationUpdated);
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

	@DeleteMapping("/firestation/{id}")
	public ResponseEntity deleteFireStation (@PathVariable("id") final int id) {
		try {
			fireStationService.deleteFireStation(id);
			return ResponseEntity.ok()
					.build();
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

	@PostMapping("/firestation")
	public ResponseEntity<FireStation> addFireStation (@RequestBody FireStation fireStation) {
		try {
			FireStation fireStationAdded = fireStationService.addFireStation(fireStation);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(fireStationAdded);
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			return ResponseEntity.notFound()
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest()
					.build();
		}
	}

}
