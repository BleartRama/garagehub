package com.garagehub.mapper;

import com.garagehub.dto.VehicleRequestDto;
import com.garagehub.dto.VehicleResponseDto;
import com.garagehub.entity.User;
import com.garagehub.entity.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public VehicleResponseDto toResponseDto(Vehicle vehicle) {
        String ownerName = vehicle.getOwner().getFirstName() + " " + vehicle.getOwner().getLastName();
        return new VehicleResponseDto(vehicle.getId(), vehicle.getRegistrationNumber(), vehicle.getBrand(), vehicle.getModel(), vehicle.getYear(), vehicle.getMileage(), ownerName);
    }

    public Vehicle toEntity(VehicleRequestDto dto, User owner) {
        return new Vehicle(dto.getRegistrationNumber(), dto.getBrand(), dto.getModel(), dto.getYear(), dto.getMileage(), owner);
    }
}
