package com.garagehub.service;

import com.garagehub.dto.VehicleRequestDto;
import com.garagehub.dto.VehicleResponseDto;
import com.garagehub.entity.User;
import com.garagehub.entity.Vehicle;
import com.garagehub.exception.ResourceNotFoundException;
import com.garagehub.mapper.VehicleMapper;
import com.garagehub.repository.UserRepository;
import com.garagehub.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleService(VehicleRepository vehicleRepository, UserRepository userRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.vehicleMapper = vehicleMapper;
    }


    public List<VehicleResponseDto> getAll() {
        return vehicleRepository.findAll().stream().map(vehicleMapper::toResponseDto).toList();
    }

    public VehicleResponseDto getById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Fordon hittades inte"));
        return vehicleMapper.toResponseDto(vehicle);
    }

    public VehicleResponseDto create(VehicleRequestDto dto) {
         User owner = userRepository.findById(dto.getOwnerId()).orElseThrow(() -> new ResourceNotFoundException("Ägare hittades inte"));

         Vehicle vehicle = vehicleMapper.toEntity(dto, owner);
         Vehicle saved = vehicleRepository.save(vehicle);
         return vehicleMapper.toResponseDto(saved);
    }

    public VehicleResponseDto update(Long id, VehicleRequestDto dto) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Fordon hittades inte"));

        User owner = userRepository.findById(dto.getOwnerId()).orElseThrow(() -> new ResourceNotFoundException("Ägare hittades inte"));

        vehicle.setRegistrationNumber(dto.getRegistrationNumber());
        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setYear(dto.getYear());
        vehicle.setMileage(dto.getMileage());
        vehicle.setOwner(owner);

        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(saved);
    }

    public void delete(Long id) {
        vehicleRepository.deleteById(id);
    }
}
