package com.openclassrooms.safety_net.repository;

import com.openclassrooms.safety_net.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Integer> {
	public Optional<Person> findByFirstNameAndLastName(String firstName, String lastName);
}
