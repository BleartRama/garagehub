package com.garagehub.dto;

import jakarta.validation.constraints.*;

public class VehicleRequestDto {
    @NotBlank(message = "Registreringsnummer får inte vara tomt")
    private String registrationNumber;
    @NotBlank(message = "Märke får inte vara tomt")
    private String brand;
    @NotBlank(message = "Modell får inte vara tomt")
    private String model;
    @Positive(message = "Årsmodell måste vara ett positivt tal")
    private int year;
    @PositiveOrZero(message = "Miltal kan inte vara negativt")
    private int mileage;
    @NotNull(message = "Ägare måste anges")
    private Long ownerId;

    public VehicleRequestDto() {}

    public VehicleRequestDto(String registrationNumber, String brand, String model, int year, int mileage, Long ownerId) {
        this.registrationNumber = registrationNumber;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.mileage = mileage;
        this.ownerId = ownerId;
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getMileage() { return mileage; }
    public void setMileage(int mileage) { this.mileage = mileage; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
