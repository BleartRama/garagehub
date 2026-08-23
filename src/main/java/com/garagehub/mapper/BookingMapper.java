package com.garagehub.mapper;

import com.garagehub.dto.BookingRequestDto;
import com.garagehub.dto.BookingResponseDto;
import com.garagehub.entity.Booking;
import com.garagehub.entity.User;
import com.garagehub.entity.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponseDto toResponseDto(Booking booking) {
        String vehicleRegistrationNumber = booking.getVehicle().getRegistrationNumber();
        String mechanicName = booking.getMechanic().getFirstName() + " " + booking.getMechanic().getLastName();


        return new BookingResponseDto(
                booking.getId(),
                booking.getBookingDate(),
                booking.getDescription(),
                booking.getStatus(),
                vehicleRegistrationNumber,
                mechanicName
        );
    }

    public Booking toEntity(BookingRequestDto dto, Vehicle vehicle, User mechanic) {
        return new Booking(
                dto.getBookingDate(),
                dto.getDescription(),
                dto.getStatus(),
                vehicle,
                mechanic
        );
    }
}