package com.openclassrooms.safety_net.repository;

import com.openclassrooms.safety_net.model.MedicalRecord;
import com.openclassrooms.safety_net.model.primary_key.PersonId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordRepository extends CrudRepository<MedicalRecord, PersonId> {
}
