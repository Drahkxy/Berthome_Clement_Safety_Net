package com.openclassrooms.safety_net.model.update;

import com.openclassrooms.safety_net.model.Address;
import lombok.Data;

@Data
public class FireStationUpdate {
	private int station;

	private Address address;
}
