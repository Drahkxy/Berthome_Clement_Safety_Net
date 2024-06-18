package com.openclassrooms.safety_net.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class JsonReaderJson {
	private ClassPathResource filePath;

	public void setFilePath (String filePath) {
		this.filePath = new ClassPathResource(filePath);
	}

	public String getJsonData () throws RuntimeException {
		try {
			return Files.readString(Path.of(filePath.getURI()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error occured while retrieving data from external resources.");
		}
	}
}
