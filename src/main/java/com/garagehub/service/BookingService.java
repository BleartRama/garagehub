package com.garagehub.service;

import com.garagehub.dto.BookingRequestDto;
import com.garagehub.dto.BookingResponseDto;
import com.garagehub.entity.Booking;
import com.garagehub.entity.User;
import com.garagehub.entity.Vehicle;
import com.garagehub.enums.BookingStatus;
import com.garagehub.enums.Role;
import com.garagehub.exception.AccessDeniedException;
import com.garagehub.exception.NotAMechanicException;
import com.garagehub.exception.ResourceNotFoundException;
import com.garagehub.mapper.BookingMapper;
import com.garagehub.repository.BookingRepository;
import com.garagehub.repository.UserRepository;
import com.garagehub.repository.VehicleRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
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

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAll() {
        User current = getCurrentUser();

        List<Booking> bookings;
        if (isPrivileged(current)) {
            bookings = bookingRepository.findAll();
        } else {
            bookings = bookingRepository.findByVehicleOwnerId(current.getId());
        }

        return bookings.stream().map(bookingMapper::toResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public BookingResponseDto getById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bokning hittades inte"));

        User current = getCurrentUser();
        if (!isPrivileged(current) && !booking.getVehicle().getOwner().getId().equals(current.getId())) {
            throw new AccessDeniedException("Du har inte behörighet att se denna bokning");
        }

        return bookingMapper.toResponseDto(booking);
    }

    public BookingResponseDto create(BookingRequestDto dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Fordon hittades inte"));


        User current = getCurrentUser();
        if (!isPrivileged(current) && !vehicle.getOwner().getId().equals(current.getId())) {
            throw new AccessDeniedException("Du kan bara boka för dina egna fordon");
        }

        Booking booking = bookingMapper.toEntity(dto, vehicle);
        booking.setStatus(BookingStatus.PENDING);   // servern sätter alltid PENDING

        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toResponseDto(saved);
    }

    public BookingResponseDto approve(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bokning hittades inte"));

        User current = getCurrentUser();
        booking.setStatus(BookingStatus.APPROVED);
        booking.setMechanic(current);      // mekanikern som godkänner plockar upp bokningen

        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toResponseDto(saved);
    }

    public BookingResponseDto cancel(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bokning hittades inte"));

        User current = getCurrentUser();
        boolean isOwner = booking.getVehicle().getOwner().getId().equals(current.getId());
        if (!isPrivileged(current) && !isOwner) {
            throw new AccessDeniedException("Du kan bara avboka dina egna bokningar");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toResponseDto(saved);
    }


    public void delete(Long id) {
        bookingRepository.deleteById(id);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Inloggad användare hittades inte"));
    }

    private boolean isPrivileged(User user) {
        return user.getRole() == Role.ADMIN || user.getRole() == Role.MECHANIC;
    }
}