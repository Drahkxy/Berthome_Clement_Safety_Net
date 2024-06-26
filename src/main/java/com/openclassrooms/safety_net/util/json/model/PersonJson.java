package com.openclassrooms.safety_net.util.json.model;

import lombok.Data;

@Data
public class PersonJson {
	private String firstName;
	private String lastName;
	private String phone;
	private String email;
	private String address;
	private String city;
	private String zip;
}
