package com.openclassrooms.safety_net.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FileJsonReaderTest {
	@Autowired
	private FileJsonReader fileJsonReader;

	@Test
	public void getJsonData_success_test () throws Exception {
		String testFilePath = "test.json";
		String expectedJson = "{\"key\": \"value\"}";

		fileJsonReader.setFilePath(testFilePath);

		String jsonData = fileJsonReader.getJsonData();

		assertEquals(expectedJson, jsonData);
	}

	@Test
	public void getJsonData_failure_test () throws Exception {
		String testFilePath = "invalid.json";

		fileJsonReader.setFilePath(testFilePath);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			fileJsonReader.getJsonData();
		});

		assertEquals("Error occurred while retrieving data from external resources.", exception.getMessage());
	}
}
