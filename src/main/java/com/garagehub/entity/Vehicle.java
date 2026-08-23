package com.garagehub.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")

public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String registrationNumber;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int mileage;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    public Vehicle() {}

    public Vehicle(String registrationNumber, String brand, String model, int year, int mileage, User owner) {
        this.registrationNumber = registrationNumber;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.mileage = mileage;
        this.owner = owner;
    }

    public Long getId() { return id; }

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

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

}
