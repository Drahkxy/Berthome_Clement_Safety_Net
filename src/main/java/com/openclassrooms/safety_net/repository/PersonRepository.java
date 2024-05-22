package com.openclassrooms.safety_net.repository;

import com.openclassrooms.safety_net.model.Person;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CrudRepository<Person, PersonId> {
	@Query(
			value = "SELECT p.email FROM person p JOIN address a ON a.id = p.address_id WHERE a.city = :city",
			nativeQuery = true
	)
	public Iterable<String> findPersonsByCity (@Param("city") String city);
}
