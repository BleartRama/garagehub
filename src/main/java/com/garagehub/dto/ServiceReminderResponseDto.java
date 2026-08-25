package com.garagehub.dto;

import java.time.LocalDate;

public class ServiceReminderResponseDto {
    private String registrationNumber;
    private int currentMileage;
    private Integer nextRecommendedServiceMileage;
    private LocalDate lastServiceDate;
    private boolean dueForService;
    private String status;

    public ServiceReminderResponseDto() {}

    public ServiceReminderResponseDto(String registrationNumber, int currentMileage, Integer nextRecommendedServiceMileage, LocalDate lastServiceDate, boolean dueForService, String status) {
        this.registrationNumber = registrationNumber;
        this.currentMileage = currentMileage;
        this.nextRecommendedServiceMileage = nextRecommendedServiceMileage;
        this.lastServiceDate = lastServiceDate;
        this.dueForService = dueForService;
        this.status = status;
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public int getCurrentMileage() { return currentMileage; }
    public Integer getNextRecommendedServiceMileage() { return nextRecommendedServiceMileage; }
    public LocalDate getLastServiceDate() { return lastServiceDate; }
    public boolean isDueForService() { return dueForService; }
    public String getStatus() { return status; }

}
