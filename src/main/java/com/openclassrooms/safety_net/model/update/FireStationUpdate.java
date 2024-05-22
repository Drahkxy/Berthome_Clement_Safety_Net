package com.openclassrooms.safety_net.model.update;

import com.openclassrooms.safety_net.model.Address;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class FireStationUpdate {
	private int station;

	private Address address;
}
