package com.openclassrooms.safety_net.util;

import com.openclassrooms.safety_net.util.json.JsonMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JsonMapperTest {
	@Autowired
	JsonMapper jsonMapper;

	@ParameterizedTest
	@CsvSource({
			"noxidian:100mg, noxidian, 100",
			"pharmacol:2500mg, pharmacol, 2500",
			"tradoxidine:400mg, tradoxidine, 400"
	})
	void mapMedicationJsonToMedicationObjects_test(String medicationJson, String medicationNameExpected, String medicationDosageExpected) {
		Map.Entry<String, Integer> medicationMap = jsonMapper.mapMedicationJsonToMedicationObjects(medicationJson);

		assertAll("Vérification du nom et du dosage du médicament",
				() -> assertEquals(medicationNameExpected, medicationMap.getKey(), "Le nom du médicament est incorrect"),
				() -> assertEquals(Integer.valueOf(medicationDosageExpected), medicationMap.getValue(), "Le dosage du médicament est incorrect")
		);
	}

}
