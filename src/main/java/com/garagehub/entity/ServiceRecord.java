package com.garagehub.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "service_records")
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDate serviceDate;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private double cost;
    @Column(nullable = false)
    private int nextRecommendedServiceMileage;
    @Column(nullable = false)
    private int mileageAtService;
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    @ManyToOne
    @JoinColumn(name = "mechanic_id")
    private User mechanic;
    @Column(nullable = false)
    private String performedAt;

    public ServiceRecord() {}

    public ServiceRecord(LocalDate serviceDate, String description, double cost,  int nextRecommendedServiceMileage, Vehicle vehicle, User mechanic) {
        this.serviceDate = serviceDate;
        this.description = description;
        this.cost = cost;
        this.nextRecommendedServiceMileage = nextRecommendedServiceMileage;
        this.vehicle = vehicle;
        this.mechanic = mechanic;
    }

    public Long getId() { return id; }

    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public int getNextRecommendedServiceMileage() { return nextRecommendedServiceMileage; }
    public void setNextRecommendedServiceMileage(int nextRecommendedServiceMileage) {this.nextRecommendedServiceMileage = nextRecommendedServiceMileage; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public User getMechanic() { return mechanic; }
    public void setMechanic(User mechanic) { this.mechanic = mechanic; }

    public int getMileageAtService() { return mileageAtService; }
    public void setMileageAtService(int mileageAtService) { this.mileageAtService = mileageAtService; }

    public String getPerformedAt() { return performedAt; }
    public void setPerformedAt(String performedAt) { this.performedAt = performedAt; }
}
