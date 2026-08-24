package com.garagehub.dto;

import java.time.LocalDate;

public class ServiceRecordRequestDto {
    private LocalDate serviceDate;
    private String description;
    private double cost;
    private int nextRecommendedServiceMileage;
    private Long vehicleId;
    private Long mechanicId;

    public ServiceRecordRequestDto() {}

    public ServiceRecordRequestDto(LocalDate serviceDate, String description, double cost, int nextRecommendedServiceMileage, Long vehicleId, Long mechanicId) {
        this.serviceDate = serviceDate;
        this.description = description;
        this.cost = cost;
        this.nextRecommendedServiceMileage = nextRecommendedServiceMileage;
        this.vehicleId = vehicleId;
        this.mechanicId = mechanicId;
    }

    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public int getNextRecommendedServiceMileage() { return nextRecommendedServiceMileage; }
    public void setNextRecommendedServiceMileage(int nextRecommendedServiceMileage) {this.nextRecommendedServiceMileage = nextRecommendedServiceMileage; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public Long getMechanicId() { return mechanicId; }
    public void setMechanicId(Long mechanicId) { this.mechanicId = mechanicId; }
}
