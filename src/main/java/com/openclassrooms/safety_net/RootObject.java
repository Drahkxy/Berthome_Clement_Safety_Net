package com.openclassrooms.safety_net;

import com.openclassrooms.safety_net.model.FireStation;
import com.openclassrooms.safety_net.model.Person;

import java.util.ArrayList;
import java.util.List;


public class RootObject {
	private List<Person> persons = new ArrayList<>();

	private List<FireStation> fireStations = new ArrayList<>();

	public List<Person> getPersons () {
		return persons;
	}
}
