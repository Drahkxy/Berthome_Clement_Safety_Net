package com.openclassrooms.safety_net.util;

import com.openclassrooms.safety_net.model.Address;
import com.openclassrooms.safety_net.model.MedicalRecord;
import com.openclassrooms.safety_net.repository.AddressRepository;
import com.openclassrooms.safety_net.repository.MedicalRecordRepository;
import com.openclassrooms.safety_net.util.json.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbSetup {
	@Autowired
	private JsonMapper mapper;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private MedicalRecordRepository medicalRecordRepository;

	public void setUpdb() {
		List<Address> addresses = mapper.getAddresses();
		List<MedicalRecord> medicalRecords = mapper.getMedicalRecords();

		addressRepository.saveAll(addresses);
		medicalRecordRepository.saveAll(medicalRecords);
	}

}
