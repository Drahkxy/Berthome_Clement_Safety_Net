package com.openclassrooms.safety_net.model.response;

import com.openclassrooms.safety_net.model.Address;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class PersonInfosNamePhoneAddress {
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private Address address;

	public PersonInfosNamePhoneAddress () {
	}

	public PersonInfosNamePhoneAddress (String firstName, String lastName, String phoneNumber, Address address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.address = address;
	}
}
