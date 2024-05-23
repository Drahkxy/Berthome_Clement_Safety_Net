package com.openclassrooms.safety_net.model.response;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class FireInfo {
	private List<Integer> fireStationsNumber;
	private List<PersonInfo> residentsInfos;

	public FireInfo () {
	}

	public FireInfo (List<Integer> fireStationsNumber, List<PersonInfo> residentsInfos) {
		this.fireStationsNumber = fireStationsNumber;
		this.residentsInfos = residentsInfos;
	}
}
