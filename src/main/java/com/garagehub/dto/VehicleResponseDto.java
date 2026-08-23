package com.garagehub.dto;

public class VehicleResponseDto {
    private Long id;
    private String registrationNumber;
    private String brand;
    private String model;
    private int year;
    private int mileage;
    private String ownerName;

    public VehicleResponseDto() {}

    public VehicleResponseDto(Long id, String registrationNumber, String brand, String model, int year, int mileage, String ownerName) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.mileage = mileage;
        this.ownerName = ownerName;
    }

    public Long getId() { return id; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public int getMileage() { return mileage; }
    public String getOwnerName() { return ownerName; }
}
