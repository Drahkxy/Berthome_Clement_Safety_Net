package com.openclassrooms.safety_net.util;

import com.openclassrooms.safety_net.model.FireStation;
import com.openclassrooms.safety_net.model.MedicalRecord;
import com.openclassrooms.safety_net.model.Person;
import com.openclassrooms.safety_net.service.FireStationService;
import com.openclassrooms.safety_net.service.MedicalRecordService;
import com.openclassrooms.safety_net.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class DbSetup {
	@Autowired
	private Mapper mapper;
	@Autowired
	private PersonService personService;
	@Autowired
	private MedicalRecordService medicalRecordService;
	@Autowired
	private FireStationService fireStationService;

	public void setMapper (Mapper mapper) {
		this.mapper = mapper;
	}

	public void setUpdb(String filePath, boolean force) {
		if (force) {
			mapper.startMapping(filePath);

			List<Person> persons = mapper.getPersons();
			List<FireStation> fireStations = mapper.getFireStations();
			List<MedicalRecord> medicalRecords = mapper.getMedicalRecords();

			/*persons.forEach(person -> {
				try {
					personService.addPerson(person);
				} catch (ResponseStatusException e) {
					e.printStackTrace();
				}
			});

			fireStations.forEach(fireStation ->  {
				try {
					fireStationService.addFireStation(fireStation);
				} catch (ResponseStatusException e) {
					e.printStackTrace();
				}
			});*/

			medicalRecords.forEach(medicalRecord -> {
				try {
					medicalRecordService.addMedicalRecord(medicalRecord);
				} catch (ResponseStatusException e) {
					e.printStackTrace();
				}
			});
		}
	}

}
