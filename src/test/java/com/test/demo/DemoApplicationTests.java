package com.test.demo;

import com.test.demo.entity.PersonPeriod;
import com.test.demo.service.util.ProjectMemberAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
class DemoApplicationTests {

	public static final String CSV_FILE_PATH = "src/main/resources/people.csv";
	@Autowired
	private ProjectMemberAnalyzer analyzer;

	@SuppressWarnings("deprecation")
	@Test
	void testCSVProcessing() {
		try{
			List<PersonPeriod> personPeriods = analyzer.readCSV(CSV_FILE_PATH);
			List<Map.Entry<String, Long>> projectDurations = analyzer.calculateProjectDurations(personPeriods);
			printPeriods(projectDurations);
		} catch (Exception e) {
			Assertions.fail("No file found for testing at " + CSV_FILE_PATH);
		}
	}

	private static void printPeriods(List<Map.Entry<String, Long>> projectDurations) {
		System.out.println("Project members with the overlapping duration they worked together:");
		projectDurations.forEach(entry -> {
			System.out.println("Project Members: " + entry.getKey() + ", Duration in Days: " + entry.getValue());
		});
	}

}
