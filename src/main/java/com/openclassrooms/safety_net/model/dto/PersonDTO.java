package com.openclassrooms.safety_net.model.dto;

import lombok.Data;

@Data
public class PersonDTO {
	private String firstName;
	private String lastName;
	private String phone;
	private String email;
	private String address;
	private String city;
	private String zip;
}
