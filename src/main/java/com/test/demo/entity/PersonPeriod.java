package com.test.demo.entity;

import java.time.LocalDate;

public class PersonPeriod {
    private int personalId;
    private int projectId;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public PersonPeriod(int personalId, int projectId, LocalDate dateFrom, LocalDate dateTo) {
        this.personalId = personalId;
        this.projectId = projectId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public int getPersonalId() {
        return personalId;
    }

    public int getProjectId() {
        return projectId;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }
}

