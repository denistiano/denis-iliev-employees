package com.test.demo.service;

import com.test.demo.entity.PersonPeriod;
import com.test.demo.service.util.ProjectMemberAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class EmployeeCalculationService {

    @Autowired
    private ProjectMemberAnalyzer analyzer;

    public List<Map.Entry<String, Long>> rankEmployeePairs(MultipartFile csvFile) throws IOException {
        List<PersonPeriod> personPeriods = analyzer.readCSV(csvFile);
        return analyzer.calculateProjectDurations(personPeriods);
    }

}
