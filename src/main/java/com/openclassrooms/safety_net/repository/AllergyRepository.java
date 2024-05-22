package com.openclassrooms.safety_net.repository;

import com.openclassrooms.safety_net.model.Allergy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllergyRepository extends CrudRepository<Allergy, Integer> {

	public Optional<Allergy> findByName(String name);

	@Query(
			value = "SELECT COUNT(DISTINCT mr.first_name, mr.last_name) " +
					"FROM medical_record mr " +
					"JOIN medical_record_allergy mra ON mr.first_name = mra.first_name AND mr.last_name = mra.last_name " +
					"JOIN allergy a ON mra.allergy_id = a.id " +
					"WHERE a.id = :id",
			nativeQuery = true
	)
	public int countMedicalRecordsLinkToAllergy (@Param("id") int id);

	@Query(
			value = "SELECT a.* " +
					"FROM allergy a " +
					"LEFT JOIN medical_record_allergy mra ON a.id = mra.allergy_id " +
					"WHERE mra.allergy_id IS NULL",
			nativeQuery = true
	)
	public Iterable<Allergy> findAllergiesOnlyWithNoLinks ();

}
