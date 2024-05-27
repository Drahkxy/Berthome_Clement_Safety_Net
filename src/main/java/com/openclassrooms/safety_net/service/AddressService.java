package com.openclassrooms.safety_net.service;

import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressService {
	@Autowired
	private AddressRepository addressRepository;

	public Iterable<Address> findAddresses () {
		return addressRepository.findAll();
	}

	public Address getAddressByLabelAndZipAndCity (String label, String zip, String city) {
		Optional<Address> address = addressRepository.findByLabelAndZipAndCity(label, zip, city);
		return address.orElse(null);
	}

	public int countResidentsAndFireStations (String label, String zip, String city) {
		return addressRepository.countTotalLinksWithAddress(label, zip, city);
	}

	public boolean addressModified (Address actualAddress, Address newAddress) {
		System.out.println("dsgdsugdsgjsdigjsiogdsgj");
		return !actualAddress.getLabel().equalsIgnoreCase(newAddress.getLabel()) ||
				!actualAddress.getZip().equalsIgnoreCase(newAddress.getZip()) ||
				!actualAddress.getCity().equalsIgnoreCase(newAddress.getCity());
	}

	public Address updateAddress (Address actualAddress, Address newAddress) {
		String newAddressLabel = newAddress.getLabel();
		String newAddressZip = newAddress.getZip();
		String newAddressCity = newAddress.getCity();

		String actualAddressLabel = actualAddress.getLabel();
		String actualAddressZip = actualAddress.getZip();
		String actualAddressCity = actualAddress.getCity();

		if (!actualAddressLabel.equals(newAddressLabel))
			actualAddress.setLabel(newAddressLabel);
		if (!actualAddressZip.equals(newAddressZip))
			actualAddress.setZip(newAddressZip);
		if (!actualAddressCity.equals(newAddressCity))
			actualAddress.setCity(newAddressCity);

		return addressRepository.save(actualAddress);
	}

}
