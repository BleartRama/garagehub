package com.garagehub.dto;

import com.garagehub.enums.BookingStatus;

import java.time.LocalDate;

public class BookingResponseDto {

    private Long id;
    private LocalDate bookingDate;
    private String description;

    private BookingStatus status;
    private String vehicleRegistrationNumber;
    private String mechanicName;


    public BookingResponseDto() {}

    public BookingResponseDto(Long id, LocalDate bookingDate, String description,
                              BookingStatus status, String vehicleRegistrationNumber, String mechanicName) {
        this.id = id;
        this.bookingDate = bookingDate;
        this.description = description;
        this.status = status;
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
        this.mechanicName = mechanicName;
    }

    public Long getId() { return id; }
    public LocalDate getBookingDate() { return bookingDate; }
    public String getDescription() { return description; }

    public BookingStatus getStatus() { return status; }
    public String getVehicleRegistrationNumber() { return vehicleRegistrationNumber; }
    public String getMechanicName() { return mechanicName; }

}