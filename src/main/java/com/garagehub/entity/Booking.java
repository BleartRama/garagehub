package com.garagehub.entity;

import com.garagehub.enums.BookingStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate bookingDate;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;



    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;


    @ManyToOne
    @JoinColumn(name = "mechanic_id")
    private User mechanic;


    public Booking() {}

    public Booking(LocalDate bookingDate, String description, BookingStatus status, Vehicle vehicle, User mechanic) {
        this.bookingDate = bookingDate;
        this.description = description;
        this.status = status;
        this.vehicle = vehicle;
        this.mechanic = mechanic;
    }

    public Long getId() { return id; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public User getMechanic() { return mechanic; }
    public void setMechanic(User mechanic) { this.mechanic = mechanic; }
}