package com.garagehub.service;

import com.garagehub.dto.VehicleRequestDto;
import com.garagehub.dto.VehicleResponseDto;
import com.garagehub.entity.User;
import com.garagehub.entity.Vehicle;
import com.garagehub.enums.Role;
import com.garagehub.exception.AccessDeniedException;
import com.garagehub.exception.ResourceInUseException;
import com.garagehub.exception.ResourceNotFoundException;
import com.garagehub.mapper.VehicleMapper;
import com.garagehub.repository.BookingRepository;
import com.garagehub.repository.ServiceRecordRepository;
import com.garagehub.repository.UserRepository;
import com.garagehub.repository.VehicleRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VehicleMapper vehicleMapper;
    private final BookingRepository bookingRepository;
    private final ServiceRecordRepository serviceRecordRepository;

    public VehicleService(VehicleRepository vehicleRepository, UserRepository userRepository, VehicleMapper vehicleMapper, BookingRepository bookingRepository, ServiceRecordRepository serviceRecordRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.vehicleMapper = vehicleMapper;
        this.bookingRepository = bookingRepository;
        this.serviceRecordRepository = serviceRecordRepository;
    }

    public void delete(Long id) {
        vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fordon hittades inte"));

        if (!bookingRepository.findByVehicleId(id).isEmpty()
                || !serviceRecordRepository.findByVehicleId(id).isEmpty()) {
            throw new ResourceInUseException("Fordonet har bokningar eller serviceposter och kan inte raderas");
        }

        vehicleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponseDto> getAll() {
        User current = getCurrentUser();

        List<Vehicle> vehicles;
        if (isPrivileged(current)) {
            vehicles = vehicleRepository.findAll();          // admin/mekaniker ser allt
        } else {
            vehicles = vehicleRepository.findByOwnerId(current.getId());  // kund ser bara sina egna
        }

        return vehicles.stream().map(vehicleMapper::toResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public VehicleResponseDto getById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fordon hittades inte"));

        User current = getCurrentUser();
        if (!isPrivileged(current) && !vehicle.getOwner().getId().equals(current.getId())) {
            throw new AccessDeniedException("Du har inte behörighet att se detta fordon");
        }

        return vehicleMapper.toResponseDto(vehicle);
    }

    public VehicleResponseDto create(VehicleRequestDto dto) {
        User owner = getCurrentUser();   // ← inloggad användare blir ägare, ingen ownerId

        Vehicle vehicle = vehicleMapper.toEntity(dto, owner);
        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(saved);
    }

    public VehicleResponseDto update(Long id, VehicleRequestDto dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fordon hittades inte"));

        vehicle.setRegistrationNumber(dto.getRegistrationNumber());
        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setYear(dto.getYear());
        vehicle.setMileage(dto.getMileage());


        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(saved);
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
