package com.garagehub.service;

import com.garagehub.dto.BookingRequestDto;
import com.garagehub.dto.BookingResponseDto;
import com.garagehub.entity.Booking;
import com.garagehub.entity.User;
import com.garagehub.entity.Vehicle;
import com.garagehub.mapper.BookingMapper;
import com.garagehub.repository.BookingRepository;
import com.garagehub.repository.UserRepository;
import com.garagehub.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    public BookingService(BookingRepository bookingRepository, VehicleRepository vehicleRepository,
                          UserRepository userRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.bookingMapper = bookingMapper;
    }

    public List<BookingResponseDto> getAll() {
        return bookingRepository.findAll().stream().map(bookingMapper::toResponseDto).toList();
    }

    public BookingResponseDto getById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking Not Found"));
        return bookingMapper.toResponseDto(booking);
    }

    public BookingResponseDto create(BookingRequestDto dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle Not Found"));

        User mechanic = userRepository.findById(dto.getMechanicId())
                .orElseThrow(() -> new RuntimeException("Mechanic Not Found"));

        Booking booking = bookingMapper.toEntity(dto, vehicle, mechanic);
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toResponseDto(saved);
    }

    public BookingResponseDto update(Long id, BookingRequestDto dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking Not Found"));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle Not Found"));

        User mechanic = userRepository.findById(dto.getMechanicId())
                .orElseThrow(() -> new RuntimeException("Mechanic Not Found"));

        booking.setBookingDate(dto.getBookingDate());
        booking.setDescription(dto.getDescription());
        booking.setStatus(dto.getStatus());
        booking.setVehicle(vehicle);
        booking.setMechanic(mechanic);

        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toResponseDto(saved);
    }

    public void delete(Long id) {
        bookingRepository.deleteById(id);
    }
}