package com.garagehub.mapper;

import com.garagehub.dto.ServiceRecordRequestDto;
import com.garagehub.dto.ServiceRecordResponseDto;
import com.garagehub.entity.ServiceRecord;
import com.garagehub.entity.User;
import com.garagehub.entity.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class ServiceRecordMapper {

    public ServiceRecordResponseDto toResponseDto(ServiceRecord record) {
        String vehicleRegistrationNumber = record.getVehicle().getRegistrationNumber();
        String mechanicName = null;
        if (record.getMechanic() != null) {
            mechanicName = record.getMechanic().getFirstName() + " " + record.getMechanic().getLastName();
        }

        return new ServiceRecordResponseDto(
                record.getId(),
                record.getServiceDate(),
                record.getDescription(),
                record.getCost(),
                record.getNextRecommendedServiceMileage(),
                mechanicName,
                vehicleRegistrationNumber,
                record.getMileageAtService(),
                record.getPerformedAt()
        );
    }

    public ServiceRecord toEntity(ServiceRecordRequestDto dto, Vehicle vehicle, User mechanic) {
        ServiceRecord record = new ServiceRecord(
                dto.getServiceDate(),
                dto.getDescription(),
                dto.getCost(),
                dto.getNextRecommendedServiceMileage(),
                vehicle,
                mechanic
        );
        record.setMileageAtService(dto.getMileageAtService());
        record.setPerformedAt(dto.getPerformedAt());
        return record;
    }
}