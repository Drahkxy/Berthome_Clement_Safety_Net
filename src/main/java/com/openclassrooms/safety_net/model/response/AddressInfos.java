package com.openclassrooms.safety_net.model.response;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class AddressInfos {
	private String label;
	private String zip;
	private String city;
	private List<PersonInfosNameEmailAgeAddressMedicals> residentsInfos;

	public AddressInfos () {
	}

	public AddressInfos (String label, String zip, String city, List<PersonInfosNameEmailAgeAddressMedicals> residentsInfos) {
		this.label = label;
		this.zip = zip;
		this.city = city;
		this.residentsInfos = residentsInfos;
	}
}
