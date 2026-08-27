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
    private int mileageAtService;
    private String performedAt;

    public ServiceRecordResponseDto() {}

    public ServiceRecordResponseDto(Long id, LocalDate serviceDate, String description, double cost, int nextRecommendedServiceMileage, String mechanicName, String vehicleRegistrationNumber, int mileageAtService, String performedAt) {
        this.id = id;
        this.serviceDate = serviceDate;
        this.description = description;
        this.cost = cost;
        this.nextRecommendedServiceMileage = nextRecommendedServiceMileage;
        this.mechanicName = mechanicName;
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
        this.mileageAtService = mileageAtService;
        this.performedAt = performedAt;
    }

    public Long getId() { return id; }

    public LocalDate getServiceDate() { return serviceDate; }

    public String getDescription() { return description; }

    public double getCost() { return cost; }

    public int getNextRecommendedServiceMileage() { return nextRecommendedServiceMileage; }

    public String getMechanicName() { return mechanicName; }

    public String getVehicleRegistrationNumber() { return vehicleRegistrationNumber; }

    public int getMileageAtService() { return mileageAtService; }

    public String getPerformedAt() { return performedAt; }

}
