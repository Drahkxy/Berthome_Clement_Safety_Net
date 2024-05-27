package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.model.FireStation;
import com.openclassrooms.safety_net.model.update.FireStationUpdate;
import com.openclassrooms.safety_net.repository.FireStationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FireStationServiceTest {
	@Mock
	private FireStationRepository fireStationRepository;

	@Mock
	private AddressService addressService;

	@InjectMocks
	private FireStationService fireStationService;

	private FireStation existingFireStation;
	private FireStation nonExistentFireStation;
	private List<FireStation> notEmptyList;
	private List<FireStation> emptyList;

	@BeforeEach
	public void setUp () {
		existingFireStation = new FireStation(1);
		existingFireStation.setId(1);
		existingFireStation.setAddress(new Address("123 Main St", "12345", "Springfield"));

		nonExistentFireStation = new FireStation(2);
		nonExistentFireStation.setAddress(new Address("459 Elm St", "67890", "Shelbyville"));

		notEmptyList = List.of(existingFireStation);
		emptyList = List.of();
	}

	@Test
	public void getFireStationById_existingFireStation_test () {
		when(fireStationRepository.findById(existingFireStation.getId())).thenReturn(Optional.of(existingFireStation));

		FireStation fireStation = fireStationService.getFireStationById(existingFireStation.getId());

		assertEquals(existingFireStation, fireStation);

		verify(fireStationRepository, times(1)).findById(existingFireStation.getId());
	}

	@Test
	public void getFireStationById_nonExistentFireStation_test () {
		when(fireStationRepository.findById(nonExistentFireStation.getId())).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			fireStationService.getFireStationById(nonExistentFireStation.getId());
		});

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

		verify(fireStationRepository, times(1)).findById(nonExistentFireStation.getId());
	}

	@Test
	public void getFireStations_notEmptyList_test () {
		when(fireStationRepository.findAll()).thenReturn(notEmptyList);

		List<FireStation> f = StreamSupport.stream(fireStationService.getFireStations().spliterator(), false).toList();

		assertEquals(notEmptyList, f);
		assertEquals(notEmptyList.get(0), f.get(0));

		verify(fireStationRepository, times(1)).findAll();
	}

	@Test
	public void getFireStations_emptyList_test () {
		when(fireStationRepository.findAll()).thenReturn(emptyList);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			fireStationService.getFireStations();
		});

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

		verify(fireStationRepository, times(1)).findAll();
	}

	@Test
	public void getFireStationsByStationNumber_existingFireStation_test () {
		when(fireStationRepository.findFireStationsByStationNumber(existingFireStation.getStation()))
				.thenReturn(notEmptyList);

		List<FireStation> f = StreamSupport.stream(
				fireStationService.getFireStationsByStationNumber(existingFireStation.getStation()).spliterator(),
				false
		).toList();

		assertEquals(notEmptyList, f);
		assertEquals(notEmptyList.get(0), f.get(0));

		verify(fireStationRepository, times(1))
				.findFireStationsByStationNumber(existingFireStation.getStation());
	}

	@Test
	public void getFireStationsByStationNumber_nonExistentFireStation_test () {
		when(fireStationRepository.findFireStationsByStationNumber(nonExistentFireStation.getStation()))
				.thenReturn(emptyList);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			fireStationService.getFireStationsByStationNumber(nonExistentFireStation.getStation());
		});

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

		verify(fireStationRepository, times(1))
				.findFireStationsByStationNumber(nonExistentFireStation.getStation());
	}

	@Test
	public void addFireStation_success_test () {
		Address address = nonExistentFireStation.getAddress();

		when(addressService.getAddressByLabelAndZipAndCity(
				address.getLabel(),
				address.getZip(),
				address.getCity()
		)).thenReturn(address);
		when(fireStationRepository.save(nonExistentFireStation)).thenReturn(nonExistentFireStation);

		FireStation fireStation = fireStationService.addFireStation(nonExistentFireStation);

		assertEquals(nonExistentFireStation, fireStation);

		verify(addressService, times(1)).getAddressByLabelAndZipAndCity(
				address.getLabel(),
				address.getZip(),
				address.getCity()
		);
		verify(fireStationRepository, times(1)).save(nonExistentFireStation);
	}

	@Test
	public void addFireStation_alreadyExist_test () {
		Address address = existingFireStation.getAddress();

		when(addressService.getAddressByLabelAndZipAndCity(
				address.getLabel(),
				address.getZip(),
				address.getCity()
		)).thenReturn(address);
		when(fireStationRepository.save(existingFireStation)).thenThrow(DataIntegrityViolationException.class);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			fireStationService.addFireStation(existingFireStation);
		});

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

		verify(addressService, times(1)).getAddressByLabelAndZipAndCity(
				address.getLabel(),
				address.getZip(),
				address.getCity()
		);
		verify(fireStationRepository, times(1)).save(existingFireStation);
	}

	@Test
	public void deleteFireStation_existingFireStation_test () {
		when(fireStationRepository.existsById(existingFireStation.getId())).thenReturn(true);

		fireStationService.deleteFireStation(existingFireStation.getId());

		verify(fireStationRepository, times(1)).existsById(existingFireStation.getId());
		verify(fireStationRepository, times(1)).deleteById(existingFireStation.getId());
	}

	@Test
	public void deleteFireStation_nonExistentFireStation_test () {
		when(fireStationRepository.existsById(nonExistentFireStation.getId())).thenReturn(false);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			fireStationService.deleteFireStation(nonExistentFireStation.getId());
		});

		verify(fireStationRepository, times(1)).existsById(nonExistentFireStation.getId());
		verify(fireStationRepository, never()).deleteById(anyInt());
	}

	@Test
	public void updateFireStation_existingFireStation_test () {
		Address actualAddress = existingFireStation.getAddress();
		Address newAddress = new Address("459 Elm St", "67890", "Shelbyville");

		FireStationUpdate fireStationUpdate = new FireStationUpdate(3, newAddress);

		when(fireStationRepository.findById(existingFireStation.getId()))
				.thenReturn(Optional.of(existingFireStation));

		when(addressService.addressModified(actualAddress, newAddress))
				.thenReturn(true);

		when(addressService.countResidentsAndFireStations(
				actualAddress.getLabel(),
				actualAddress.getZip(),
				actualAddress.getCity()
				)
		).thenReturn(2);

		when(addressService.getAddressByLabelAndZipAndCity(
				newAddress.getLabel(),
				newAddress.getZip(),
				newAddress.getCity())
		).thenReturn(newAddress);

		when(fireStationRepository.save(existingFireStation)).thenReturn(existingFireStation);

		FireStation fireStation = fireStationService.updateFireStation(existingFireStation.getId(), fireStationUpdate);

		assertEquals(existingFireStation, fireStation);

		verify(fireStationRepository, times(1)).findById(existingFireStation.getId());
		verify(addressService, times(1)).addressModified(actualAddress, newAddress);
		verify(addressService, times(1)).countResidentsAndFireStations(
				actualAddress.getLabel(),
				actualAddress.getZip(),
				actualAddress.getCity()
		);
		verify(addressService, never()).updateAddress(any(Address.class), any(Address.class));
		verify(addressService, times(1)).getAddressByLabelAndZipAndCity(
				newAddress.getLabel(),
				newAddress.getZip(),
				newAddress.getCity()
		);
		verify(fireStationRepository, times(1)).save(existingFireStation);

	}

	@Test
	public void updateFireStation_nonExistentFireStation_test () {
		when(fireStationRepository.findById(nonExistentFireStation.getId())).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			fireStationService.updateFireStation(nonExistentFireStation.getId(), new FireStationUpdate());
		});

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

		verify(fireStationRepository, times(1)).findById(nonExistentFireStation.getId());
		verify(addressService, never()).addressModified(any(Address.class), any(Address.class));
		verify(addressService, never()).countResidentsAndFireStations(anyString(), anyString(), anyString());
		verify(addressService, never()).updateAddress(any(Address.class), any(Address.class));
		verify(addressService, never()).getAddressByLabelAndZipAndCity(anyString(), anyString(), anyString());
		verify(fireStationRepository, never()).save(any(FireStation.class));
	}

}
