package com.openclassrooms.safety_net.model.update;

import com.openclassrooms.safety_net.model.Address;
import lombok.Data;

@Data
public class PersonUpdate {
	private String phone;

	private String email;

	private Address address;
}
