package com.openclassrooms.safety_net.model.response;

import com.openclassrooms.safety_net.model.Address;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class PersonInfosNamePhoneAgeAddress {
	private String firstName;
	private String lastName;
	private String phone;
	private int age;
	private Address address;

	public PersonInfosNamePhoneAgeAddress (String firstName, String lastName, String phone, int age, Address address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.age = age;
		this.address = address;
	}

	public PersonInfosNamePhoneAgeAddress () {
	}
}
