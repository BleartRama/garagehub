package com.garagehub.service;

import com.garagehub.dto.ServiceRecordRequestDto;
import com.garagehub.dto.ServiceRecordResponseDto;
import com.garagehub.entity.ServiceRecord;
import com.garagehub.entity.User;
import com.garagehub.entity.Vehicle;
import com.garagehub.exception.ResourceNotFoundException;
import com.garagehub.mapper.ServiceRecordMapper;
import com.garagehub.repository.ServiceRecordRepository;
import com.garagehub.repository.UserRepository;
import com.garagehub.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceRecordService {

    private final ServiceRecordRepository serviceRecordRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ServiceRecordMapper serviceRecordMapper;

    public ServiceRecordService(ServiceRecordRepository serviceRecordRepository, VehicleRepository vehicleRepository,
                          UserRepository userRepository, ServiceRecordMapper serviceRecordMapper) {
        this.serviceRecordRepository = serviceRecordRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.serviceRecordMapper = serviceRecordMapper;
    }

    public List<ServiceRecordResponseDto> getByVehicleId(Long vehicleId) {
        vehicleRepository.findById(vehicleId).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        return serviceRecordRepository.findByVehicleId(vehicleId).stream().map(serviceRecordMapper::toResponseDto).toList();
    }

    public List<ServiceRecordResponseDto> getAll() {
        return serviceRecordRepository.findAll().stream().map(serviceRecordMapper::toResponseDto).toList();
    }

    public ServiceRecordResponseDto getById(Long id) {
        ServiceRecord serviceRecord = serviceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceRecord Not Found"));
        return serviceRecordMapper.toResponseDto(serviceRecord);
    }

    public ServiceRecordResponseDto create(ServiceRecordRequestDto dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Not Found"));

        User mechanic = userRepository.findById(dto.getMechanicId())
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic Not Found"));

        ServiceRecord serviceRecord = serviceRecordMapper.toEntity(dto, vehicle, mechanic);
        ServiceRecord saved = serviceRecordRepository.save(serviceRecord);
        return serviceRecordMapper.toResponseDto(saved);
    }

    public ServiceRecordResponseDto update(Long id, ServiceRecordRequestDto dto) {
        ServiceRecord serviceRecord = serviceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceRecord Not Found"));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Not Found"));

        User mechanic = userRepository.findById(dto.getMechanicId())
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic Not Found"));

        serviceRecord.setCost(dto.getCost());
        serviceRecord.setNextRecommendedServiceMileage(dto.getNextRecommendedServiceMileage());

        serviceRecord.setServiceDate(dto.getServiceDate());
        serviceRecord.setDescription(dto.getDescription());
        serviceRecord.setVehicle(vehicle);
        serviceRecord.setMechanic(mechanic);

        ServiceRecord saved = serviceRecordRepository.save(serviceRecord);
        return serviceRecordMapper.toResponseDto(saved);
    }

    public void delete(Long id) {
        serviceRecordRepository.deleteById(id);
    }


}