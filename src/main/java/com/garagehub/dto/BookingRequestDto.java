package com.garagehub.dto;

import com.garagehub.enums.BookingStatus;

import java.time.LocalDate;

public class BookingRequestDto {

    private LocalDate bookingDate;
    private String description;

    private BookingStatus status;
    private Long vehicleId;
    private Long mechanicId;


    public BookingRequestDto() {}


    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public Long getMechanicId() { return mechanicId; }
    public void setMechanicId(Long mechanicId) { this.mechanicId = mechanicId; }

}