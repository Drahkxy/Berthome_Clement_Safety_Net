package com.openclassrooms.safety_net.util;

import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.repository.AddressRepository;
import com.openclassrooms.safety_net.repository.PersonRepository;
import com.openclassrooms.safety_net.util.json.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbSetup {
	@Autowired
	private JsonMapper mapper;
	@Autowired
	private PersonRepository personRepository;
	@Autowireds
	private AddressRepository addressRepository;

	public void setUpdb() {
		List<Address> addresses = mapper.getAddressEntitiesWithResidents();

		addresses.forEach(address -> System.out.println(address.getCity()));

		addressRepository.saveAll(addresses);
	}

}
