package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.model.FireStation;
import com.openclassrooms.safety_net.model.update.FireStationUpdate;
import com.openclassrooms.safety_net.repository.FireStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class FireStationService {
	@Autowired
	private FireStationRepository fireStationRepository;

	@Autowired
	private AddressService addressService;

	public FireStation getFireStationById (int id) throws ResponseStatusException {
		Optional<FireStation> fire = fireStationRepository.findById(id);
		if (fire.isPresent())
			return fire.get();
		else
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No fire station found with id %d.".formatted(id));
	}

	public Iterable<FireStation> getFireStations () throws ResponseStatusException {
		Iterable<FireStation> fireStations = fireStationRepository.findAll();
		if (fireStations.iterator().hasNext())
			return fireStations;
		else
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No fire station found.");
	}

	public Iterable<FireStation> getFireStationsByStationNumber (int stationNumber) throws ResponseStatusException {
		Iterable<FireStation> fireStations = fireStationRepository.findFireStationsByStationNumber(stationNumber);
		if (fireStations.iterator().hasNext()) {
			return fireStations;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No fire station found with %d station number.".formatted(stationNumber));
		}
	}

	public FireStation addFireStation (FireStation fireStation) throws ResponseStatusException {
		try {
			Address newFireStationAddress = fireStation.getAddress();

			Address address = addressService.getAddressByLabelAndZipAndCity(newFireStationAddress.getLabel(), newFireStationAddress.getZip(), newFireStationAddress.getCity());

			if (address != null) {
				fireStation.setAddress(address);
			}

			return fireStationRepository.save(fireStation);
		} catch (DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fire station with %d id already exist in database.".formatted(fireStation.getId()));
		}
	}

	public FireStation updateFireStation (int id, FireStationUpdate fsUpdate) throws ResponseStatusException {
		FireStation fireStation = getFireStationById(id);

		Address actualAddress = fireStation.getAddress();
		Address newAddress = fsUpdate.getAddress();

		if (addressService.addressModified(actualAddress, newAddress)) {
			int countResidentsAndFireStations = addressService.countResidentsAndFireStations(actualAddress.getLabel(), actualAddress.getZip(), actualAddress.getCity());
			if (countResidentsAndFireStations <= 1) {
				newAddress = addressService.updateAddress(actualAddress, newAddress);
			} else {
				Address existingAddress = addressService.getAddressByLabelAndZipAndCity(newAddress.getLabel(), newAddress.getZip(), newAddress.getCity());
				if (existingAddress != null) {
					newAddress = existingAddress;
				}
			}
		}

		fireStation.setAddress(newAddress);
		fireStation.setStation(fsUpdate.getStation());

		return fireStationRepository.save(fireStation);
	}


	public void deleteFireStation (int id) throws ResponseStatusException {
		if (fireStationRepository.existsById(id)) {
			fireStationRepository.deleteById(id);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No fire station found with id %d.".formatted(id));
		}
	}

}
