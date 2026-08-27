package com.garagehub.dto;


import jakarta.validation.constraints.*;


import java.time.LocalDate;

public class BookingRequestDto {
    @NotNull(message = "Bokningsdatum måste anges")
    private LocalDate bookingDate;
    @NotBlank(message = "Beskrivning får inte vara tom")
    private String description;

    @NotNull(message = "Fordon måste anges")
    private Long vehicleId;



    public BookingRequestDto() {}


    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }



}