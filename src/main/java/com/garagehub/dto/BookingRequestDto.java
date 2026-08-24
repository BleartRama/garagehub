package com.garagehub.dto;

import com.garagehub.enums.BookingStatus;
import jakarta.validation.constraints.*;


import java.time.LocalDate;

public class BookingRequestDto {
    @NotNull(message = "Bokningsdatum måste anges")
    private LocalDate bookingDate;
    @NotBlank(message = "Beskrivning får inte vara tom")
    private String description;
    @NotNull(message = "Status måste anges")
    private BookingStatus status;
    @NotNull(message = "Fordon måste anges")
    private Long vehicleId;
    @NotNull(message = "Mekaniker måste anges")
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