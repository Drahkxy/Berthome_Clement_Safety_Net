package com.openclassrooms.safety_net.repository;

import com.openclassrooms.safety_net.model.Medication;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicationRepository extends CrudRepository<Medication, Integer> {

	public Optional<Medication> findByNameAndDosage (String name, int dosage);

	@Query(
			value = "SELECT COUNT(DISTINCT mr.first_name, mr.last_name) " +
					"FROM medical_record mr " +
					"JOIN medical_record_medication mrm ON mr.first_name = mrm.first_name AND mr.last_name = mrm.last_name " +
					"JOIN medication m ON mrm.medication_id = m.id " +
					"WHERE m.id = :id",
			nativeQuery = true
	)
	public int countMedicalRecordsLinkToMedication (@Param("id") int id);

	@Query(
			value = "SELECT m.* " +
					"FROM medication m " +
					"LEFT JOIN medical_record_medication mrm ON m.id = mrm.medication_id " +
					"WHERE mrm.medication_id IS NULL",
			nativeQuery = true
	)
	public Iterable<Medication> findMedicationsOnlyWithNoLinks ();

}
