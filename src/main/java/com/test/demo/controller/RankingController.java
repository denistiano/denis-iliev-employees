package com.test.demo.controller;

import com.test.demo.service.EmployeeCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
public class RankingController {

    @Autowired
    private EmployeeCalculationService service;

    @PostMapping("/upload")
    public ResponseEntity<List<Map.Entry<String, Long>>> uploadCSV(@RequestParam("file") MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                List<Map.Entry<String, Long>> ranking = service.rankEmployeePairs(file);
                System.out.println("Project members with the overlapping duration they worked together:");
                ranking.forEach(entry -> {
                    System.out.println("Project Members: " + entry.getKey() + ", Duration in Days: " + entry.getValue());
                });
                return new ResponseEntity<>(ranking, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) { //Covers IO Exceptions too
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
