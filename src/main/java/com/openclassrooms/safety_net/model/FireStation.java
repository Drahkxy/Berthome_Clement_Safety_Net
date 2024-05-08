package com.openclassrooms.safety_net.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "fire_station")
public class FireStation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private int id;

	private int station;

	@ManyToOne(
			cascade = {
					CascadeType.MERGE,
					CascadeType.PERSIST
			}
	)
	@JoinColumn(name = "address_id")
	private Address address;

	public FireStation (int station) {
		this.station = station;
	}
}
