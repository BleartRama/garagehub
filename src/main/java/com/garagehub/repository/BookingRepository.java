package com.garagehub.repository;

import com.garagehub.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByVehicleId(Long vehicleId);
    List<Booking> findByVehicleOwnerId(Long ownerId);
}