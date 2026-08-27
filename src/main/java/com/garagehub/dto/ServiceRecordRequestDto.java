package com.garagehub.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class ServiceRecordRequestDto {
    @NotNull(message = "Servicedatum måste anges")
    private LocalDate serviceDate;
    @NotBlank(message = "Beskrivning får inte vara tom")
    private String description;
    @PositiveOrZero(message = "Kostnad kan inte vara negativ")
    private double cost;
    @PositiveOrZero(message = "Miltal kan inte vara negativt")
    private int nextRecommendedServiceMileage;
    @NotNull(message = "Fordon måste anges")
    private Long vehicleId;

    private Long mechanicId;
    @PositiveOrZero(message = "Miltal vid senaste service kan inte vara negativt")
    private int mileageAtService;
    @NotBlank(message = "Verkstad/ort måste anges")
    private String performedAt;

    public ServiceRecordRequestDto() {}

    public ServiceRecordRequestDto(LocalDate serviceDate, String description, double cost, int nextRecommendedServiceMileage, Long vehicleId, Long mechanicId, int mileageAtService, String performedAt) {
        this.serviceDate = serviceDate;
        this.description = description;
        this.cost = cost;
        this.nextRecommendedServiceMileage = nextRecommendedServiceMileage;
        this.vehicleId = vehicleId;
        this.mechanicId = mechanicId;
        this.mileageAtService = mileageAtService;
        this.performedAt = performedAt;
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

    public int getMileageAtService() { return mileageAtService; }
    public void setMileageAtService(int mileageAtService) { this.mileageAtService = mileageAtService; }

    public String getPerformedAt() { return performedAt; }
    public void setPerformedAt(String performedAt) { this.performedAt = performedAt; }
}
