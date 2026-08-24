package com.garagehub.dto;

import java.time.LocalDate;

public class ServiceRecordResponseDto {

    private Long id;
    private LocalDate serviceDate;
    private String description;
    private double cost;
    private int nextRecommendedServiceMileage;
    private String mechanicName;
    private String vehicleRegistrationNumber;

    public ServiceRecordResponseDto() {}

    public ServiceRecordResponseDto(Long id, LocalDate serviceDate, String description, double cost, int nextRecommendedServiceMileage, String mechanicName, String vehicleRegistrationNumber) {
        this.id = id;
        this.serviceDate = serviceDate;
        this.description = description;
        this.cost = cost;
        this.nextRecommendedServiceMileage = nextRecommendedServiceMileage;
        this.mechanicName = mechanicName;
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
    }

    public Long getId() { return id; }

    public LocalDate getServiceDate() { return serviceDate; }

    public String getDescription() { return description; }

    public double getCost() { return cost; }

    public int getNextRecommendedServiceMileage() { return nextRecommendedServiceMileage; }

    public String getMechanicName() { return mechanicName; }

    public String getVehicleRegistrationNumber() { return vehicleRegistrationNumber; }

}
