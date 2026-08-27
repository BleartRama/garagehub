package com.garagehub.repository;

import com.garagehub.entity.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {

    List<ServiceRecord> findByVehicleId(Long vehicleId);
    Optional<ServiceRecord> findTopByVehicleIdOrderByServiceDateDesc(Long vehicleId);
    List<ServiceRecord> findByVehicleOwnerId(Long ownerId);
}
