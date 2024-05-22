package com.openclassrooms.safety_net.repository;

import com.openclassrooms.safety_net.model.FireStation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FireStationRepository extends CrudRepository<FireStation, Integer> {

	@Query(
			value = "SELECT * FROM fire_station fs WHERE fs.station = :station",
			nativeQuery = true
	)
	public Iterable<FireStation> findFireStationsByStationNumber (@Param("station") int stationNumber);

}
