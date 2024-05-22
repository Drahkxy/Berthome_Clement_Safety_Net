package com.openclassrooms.safety_net.repository;

import com.openclassrooms.safety_net.model.Address;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends CrudRepository<Address, Integer> {
	public Optional<Address> findByLabelAndZipAndCity(String label, String zip, String city);

	public void deleteByLabelAndZipAndCity(String label, String zip, String city);

	@Query(
			value = "SELECT " +
					"  (SELECT COUNT(p.first_name) FROM address a " +
					"   LEFT JOIN person p ON a.id = p.address_id " +
					"   WHERE a.label = :label AND a.zip = :zip AND a.city = :city) + " +
					"  (SELECT COUNT(f.id) FROM address a " +
					"   LEFT JOIN fire_station f ON a.id = f.address_id " +
					"   WHERE a.label = :label AND a.zip = :zip AND a.city = :city) AS total_count",
			nativeQuery = true
	)
	public int countTotalLinksWithAddress(@Param("label") String label, @Param("zip") String zip, @Param("city") String city);


}
