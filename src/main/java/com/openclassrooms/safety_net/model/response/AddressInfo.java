package com.openclassrooms.safety_net.model.response;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class AddressInfo {
	private String label;
	private String zip;
	private String city;
	private List<PersonInfo> residentsInfos;

	public AddressInfo () {
	}

	public AddressInfo (String label, String zip, String city, List<PersonInfo> residentsInfos) {
		this.label = label;
		this.zip = zip;
		this.city = city;
		this.residentsInfos = residentsInfos;
	}
}
