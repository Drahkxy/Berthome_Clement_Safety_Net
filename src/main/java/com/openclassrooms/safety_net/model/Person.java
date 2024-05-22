package com.openclassrooms.safety_net.model;

import com.openclassrooms.safety_net.model.primary_key.PersonId;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Data
@Table(name = "person")
@IdClass(PersonId.class)
@DynamicUpdate
public class Person {
	@Id
	@Column(name = "first_name")
	private String firstName;

	@Id
	@Column(name = "last_name")
	private String lastName;

	private String phone;

	private String email;

	@ManyToOne(
			cascade = CascadeType.ALL
	)
	@JoinColumn(name = "address_id")
	private Address address;


	public Person () {}

	public Person (String firstName, String lastName, String phone, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.email = email;
	}


	@Override
	public String toString () {
		return "Person{" +
				"firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", phone='" + phone + '\'' +
				", email='" + email + '\'' +
				", address=" + address +
				'}';
	}
}
