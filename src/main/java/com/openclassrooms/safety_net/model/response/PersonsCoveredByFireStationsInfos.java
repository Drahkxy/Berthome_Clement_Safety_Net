package com.openclassrooms.safety_net.model.response;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class PersonsCoveredByFireStationsInfos {
	private int childrenCount;
	private int adultCount;
	private List<PersonInfosNamePhoneAddress> persons;

	public PersonsCoveredByFireStationsInfos () {
	}

	public PersonsCoveredByFireStationsInfos (int childrenCount, int adultCount, List<PersonInfosNamePhoneAddress> persons) {
		this.childrenCount = childrenCount;
		this.adultCount = adultCount;
		this.persons = persons;
	}
}
