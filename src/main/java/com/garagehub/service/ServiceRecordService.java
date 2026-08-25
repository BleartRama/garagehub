package com.garagehub.service;

import com.garagehub.dto.ServiceRecordRequestDto;
import com.garagehub.dto.ServiceRecordResponseDto;
import com.garagehub.dto.ServiceReminderResponseDto;
import java.util.Optional;
import java.time.LocalDate;
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

    public ServiceReminderResponseDto getServiceReminder(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new ResourceNotFoundException("Fordon hittades inte"));
        Optional<ServiceRecord> latest = serviceRecordRepository.findTopByVehicleIdOrderByServiceDateDesc(vehicleId);
        if (latest.isEmpty()) {
            return new ServiceReminderResponseDto(
                    vehicle.getRegistrationNumber(),
                    vehicle.getMileage(),
                    null,
                    null,
                    false,
                    "Ingen service registrerad än, registrera senaste service"
            );
        }
        ServiceRecord serviceRecord = latest.get();
        boolean olderThan12Months = serviceRecord.getServiceDate().isBefore(LocalDate.now().minusMonths(12));
        boolean kmReached = vehicle.getMileage() >= serviceRecord.getNextRecommendedServiceMileage();
        boolean dueForService = kmReached || olderThan12Months;
        String status;
        if (kmReached && olderThan12Months) {
            status = "Dags för service! (Mätarställning passerad och över 12 månader sedan senaste service)";
        } else if (kmReached) {
            status = "Dags för service! (Mätarställning passerad)";
        } else if (olderThan12Months) {
            status = "Dags för service! (Över 12 månader sedan senaste service)";
        } else {
            status = "Inte dags för service än";
        }
        return new ServiceReminderResponseDto(
                vehicle.getRegistrationNumber(),
                vehicle.getMileage(),
                serviceRecord.getNextRecommendedServiceMileage(),
                serviceRecord.getServiceDate(),
                dueForService,
                status
        );
    }


    public List<ServiceRecordResponseDto> getByVehicleId(Long vehicleId) {
        vehicleRepository.findById(vehicleId).orElseThrow(() -> new ResourceNotFoundException("Fordon hittades inte"));
        return serviceRecordRepository.findByVehicleId(vehicleId).stream().map(serviceRecordMapper::toResponseDto).toList();
    }

    public List<ServiceRecordResponseDto> getAll() {
        return serviceRecordRepository.findAll().stream().map(serviceRecordMapper::toResponseDto).toList();
    }


    public ServiceRecordResponseDto getById(Long id) {
        ServiceRecord serviceRecord = serviceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceRecord hittades inte"));
        return serviceRecordMapper.toResponseDto(serviceRecord);
    }

    public ServiceRecordResponseDto create(ServiceRecordRequestDto dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Fordon hittades inte"));

        User mechanic = userRepository.findById(dto.getMechanicId())
                .orElseThrow(() -> new ResourceNotFoundException("Mekaniker hittades inte"));

        ServiceRecord serviceRecord = serviceRecordMapper.toEntity(dto, vehicle, mechanic);
        ServiceRecord saved = serviceRecordRepository.save(serviceRecord);
        return serviceRecordMapper.toResponseDto(saved);
    }

    public ServiceRecordResponseDto update(Long id, ServiceRecordRequestDto dto) {
        ServiceRecord serviceRecord = serviceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceRecord hittades inte"));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Fordon hittades inte"));

        User mechanic = userRepository.findById(dto.getMechanicId())
                .orElseThrow(() -> new ResourceNotFoundException("Mekaniker hittades inte"));

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