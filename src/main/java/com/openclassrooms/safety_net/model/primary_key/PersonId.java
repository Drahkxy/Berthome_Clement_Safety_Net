package com.openclassrooms.safety_net.model.primary_key;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class PersonId implements Serializable {
	private String firstName;

	private String lastName;

	public PersonId () {
	}

	public PersonId (String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PersonId personId = (PersonId) o;

		if (getFirstName() != null ? !getFirstName().equals(personId.getFirstName()) : personId.getFirstName() != null)
			return false;
		return getLastName() != null ? getLastName().equals(personId.getLastName()) : personId.getLastName() == null;
	}

	@Override
	public int hashCode () {
		return Objects.hash(firstName, lastName);
	}
}
