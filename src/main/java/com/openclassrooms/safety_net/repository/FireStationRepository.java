package com.openclassrooms.safety_net.repository;

import com.openclassrooms.safety_net.model.FireStation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FireStationRepository extends CrudRepository<FireStation, Integer> {
}
