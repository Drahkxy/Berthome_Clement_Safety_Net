package com.openclassrooms.safety_net.model.response;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class FireInfos {
	private List<Integer> fireStationsNumber;
	private List<PersonInfosNameEmailAgeAddressMedicals> residentsInfos;

	public FireInfos () {
	}

	public FireInfos (List<Integer> fireStationsNumber, List<PersonInfosNameEmailAgeAddressMedicals> residentsInfos) {
		this.fireStationsNumber = fireStationsNumber;
		this.residentsInfos = residentsInfos;
	}
}
