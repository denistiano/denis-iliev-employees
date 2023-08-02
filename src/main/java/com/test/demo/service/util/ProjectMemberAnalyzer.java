package com.test.demo.service.util;

import com.test.demo.entity.PersonPeriod;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProjectMemberAnalyzer {

    @Deprecated
    public List<PersonPeriod> readCSV(String filePath) throws IOException {
        return mapToPersonPeriod(new FileReader(filePath));
    }

    public List<PersonPeriod> readCSV(MultipartFile file) throws IOException {
        return mapToPersonPeriod(new InputStreamReader(file.getInputStream()));
    }

    public Map<String, Long> calculateProjectDurations(List<PersonPeriod> personPeriods) {
        Map<String, Long> projectDurations = new HashMap<>();
        Map<Integer, List<PersonPeriod>> projectMembersMap = personPeriods.stream()
                .collect(Collectors.groupingBy(PersonPeriod::getProjectId));

        for (List<PersonPeriod> projectMembers : projectMembersMap.values()) { //PROJECT level
            calculateOverlappingDurations(projectDurations, projectMembers);
        }

        return projectDurations;
    }

    private void calculateOverlappingDurations(Map<String, Long> projectDurations, List<PersonPeriod> projectMembers) {
        for (int i = 0; i < projectMembers.size(); i++) { //MEMBER 1 level
            PersonPeriod person1 = projectMembers.get(i);
            for (int j = i + 1; j < projectMembers.size(); j++) { //MEMBER 2 level
                PersonPeriod person2 = projectMembers.get(j);
                if(person1.getPersonalId() == person2.getPersonalId()) continue; //SKip overlapping periods from the same member
                if (doPeriodsOverlap(person1, person2)) {
                    long overlappingDays = calculateOverlappingDays(person1, person2);
                    String projectMembersKey = person1.getPersonalId() + "," + person2.getPersonalId();
                    String projectMembersKeyReversed = person2.getPersonalId() + "," + person1.getPersonalId();
                    if (projectDurations.containsKey(projectMembersKeyReversed)) {
                        projectDurations.put(projectMembersKeyReversed, projectDurations.getOrDefault(projectMembersKeyReversed, 0L) + overlappingDays);
                    } else {
                        projectDurations.put(projectMembersKey, projectDurations.getOrDefault(projectMembersKey, 0L) + overlappingDays);
                    }
                }
            }
        }
    }

    private List<PersonPeriod> mapToPersonPeriod(Reader reader) {
        List<PersonPeriod> personPeriods = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(reader)) {
            br.readLine(); // Skip header
            personPeriods = br.lines().map(line -> {
                String[] fields = line.split(",");
                int personalId = Integer.parseInt(fields[0].trim());
                int projectId = Integer.parseInt(fields[1].trim());

                LocalDate dateFrom = parseLocalDate(fields[2].trim());
                LocalDate dateTo = parseLocalDate(fields[3].trim());

                return new PersonPeriod(personalId, projectId, dateFrom, dateTo);
            }).collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Issue with parsing CSV file");
        }

        return personPeriods;
    }

    private LocalDate parseLocalDate(String dateStr) {
        DateTimeFormatter[] dateFormatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yy")
        };

        for (DateTimeFormatter formatter : dateFormatters) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        //NULLs default to now() as well
        return LocalDate.now();
    }

    private boolean doPeriodsOverlap(PersonPeriod person1, PersonPeriod person2) {
        LocalDate person1DateFrom = person1.getDateFrom();
        LocalDate person1DateTo = person1.getDateTo() != null ? person1.getDateTo() : LocalDate.now();
        LocalDate person2DateFrom = person2.getDateFrom();
        LocalDate person2DateTo = person2.getDateTo() != null ? person2.getDateTo() : LocalDate.now();

        return (person1DateFrom.isBefore(person2DateTo) || person1DateFrom.isEqual(person2DateTo)) && (person1DateTo.isAfter(person2DateFrom) || person1DateTo.isEqual(person2DateFrom));
    }

    private long calculateOverlappingDays(PersonPeriod person1, PersonPeriod person2) {
        LocalDate person1DateFrom = person1.getDateFrom();
        LocalDate person1DateTo = person1.getDateTo() != null ? person1.getDateTo() : LocalDate.now();
        LocalDate person2DateFrom = person2.getDateFrom();
        LocalDate person2DateTo = person2.getDateTo() != null ? person2.getDateTo() : LocalDate.now();

        LocalDate overlapStartDate = person1DateFrom.isBefore(person2DateFrom) ? person2DateFrom : person1DateFrom;
        LocalDate overlapEndDate = person1DateTo.isBefore(person2DateTo) ? person1DateTo : person2DateTo;

        return ChronoUnit.DAYS.between(overlapStartDate, overlapEndDate.plusDays(1));
    }
}