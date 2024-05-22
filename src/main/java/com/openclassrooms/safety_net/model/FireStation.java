package com.openclassrooms.safety_net.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "fire_station")
public class FireStation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private int station;

	@ManyToOne(
			cascade = CascadeType.ALL
	)
	@JoinColumn(name = "address_id")
	private Address address;

	public FireStation () {}

	public FireStation (int station) {
		this.station = station;
	}
}
