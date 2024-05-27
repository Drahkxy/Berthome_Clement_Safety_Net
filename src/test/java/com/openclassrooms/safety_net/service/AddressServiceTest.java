package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AddressServiceTest {
	@Mock
	private AddressRepository addressRepository;
	@InjectMocks
	private AddressService addressService;

	private Address oldAddress;
	private Address newAddress;

	@BeforeEach
	public void setUp () {
		oldAddress = new Address("123 Main St", "12345", "Springfield");
		newAddress = new Address("459 Elm St", "67890", "Shelbyville");
	}

	@Test
	public void getAddressByLabelAndZipAndCity_existingAddress_test () {
		String label = oldAddress.getLabel();
		String zip = oldAddress.getZip();
		String city = oldAddress.getCity();

		when(addressRepository.findByLabelAndZipAndCity(label, zip, city)).thenReturn(Optional.of(oldAddress));

		Address address = addressService.getAddressByLabelAndZipAndCity(label, zip, city);

		assertEquals(oldAddress, address);

		verify(addressRepository, times(1)).findByLabelAndZipAndCity(label, zip, city);
	}

	@Test
	public void getAddressByLabelAndZipAndCity_nonExistentAddress_test () {
		String label = newAddress.getLabel();
		String zip = newAddress.getZip();
		String city = newAddress.getCity();

		when(addressRepository.findByLabelAndZipAndCity(label, zip, city)).thenReturn(Optional.empty());

		Address address = addressService.getAddressByLabelAndZipAndCity(label, zip, city);

		assertNull(address);

		verify(addressRepository, times(1)).findByLabelAndZipAndCity(label, zip, city);
	}

	@ParameterizedTest
	@ValueSource(ints = {2, 10, 20, 50, 1512, 848564})
	public void countResidentsAndFireStations_test (int expectedCount) {
		String label = oldAddress.getLabel();
		String zip = oldAddress.getZip();
		String city = oldAddress.getCity();

		when(addressRepository.countTotalLinksWithAddress(label, zip, city)).thenReturn(expectedCount);

		int count = addressService.countResidentsAndFireStations(label, zip, city);

		assertEquals(expectedCount, count);

		verify(addressRepository, times(1)).countTotalLinksWithAddress(label, zip, city);
	}

	@Test
	public void addressModified_withModifiedAddress_test () {
		boolean modified = addressService.addressModified(oldAddress, newAddress);

		assertTrue(modified, "Result should be true.");
	}

	@Test
	public void addressModified_withoutModifiedAddress_test () {
		boolean modified = addressService.addressModified(oldAddress, oldAddress);

		assertFalse(modified, "Result should be false.");
	}

	@Test
	public void updateAddress_withModifiedAddress_test () {
		when(addressRepository.save(newAddress)).thenReturn(newAddress);

		Address updatedAddress = addressService.updateAddress(oldAddress, newAddress);

		assertEquals(newAddress, updatedAddress);

		verify(addressRepository, times(1)).save(newAddress);
	}
}
