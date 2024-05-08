package com.openclassrooms.safety_net;

import com.openclassrooms.safety_net.service.PersonService;
import com.openclassrooms.safety_net.util.DbSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SafetyNetApplication implements CommandLineRunner {
	@Autowired
	private DbSetup dbSetup;
	@Autowired
	private PersonService personService;

	public static void main(String[] args) {
		SpringApplication.run(SafetyNetApplication.class, args);
	}

	@Override
	public void run (String... args) throws Exception {
		dbSetup.setUpdb();


	}



}
