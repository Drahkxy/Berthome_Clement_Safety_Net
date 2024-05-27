package com.openclassrooms.safety_net.model.update;

import com.openclassrooms.safety_net.model.Address;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class PersonUpdate {
	private String phone;

	private String email;

	private Address address;

	public PersonUpdate (String phone, String email, Address address) {
		this.phone = phone;
		this.email = email;
		this.address = address;
	}

	public PersonUpdate () {
	}
}
