package com.openclassrooms.safety_net.model.response;

import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.model.Allergy;
import com.openclassrooms.safety_net.model.Medication;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class PersonInfosNameEmailAgeAddressMedicals {
	private String firstName;
	private String lastName;
	private String email;
	private int age;
	private Address address;
	private List<Allergy> allergies;
	private List<Medication> medications;

	public PersonInfosNameEmailAgeAddressMedicals (String firstName, String lastName, String email, int age, Address address, List<Allergy> allergies, List<Medication> medications) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.age = age;
		this.address = address;
		this.allergies = allergies;
		this.medications = medications;
	}

	public PersonInfosNameEmailAgeAddressMedicals () {
	}
}
