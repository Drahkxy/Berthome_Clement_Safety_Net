package com.openclassrooms.safety_net.repository;

import com.openclassrooms.safety_net.model.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends CrudRepository<Address, Integer> {
}
