package com.garagehub.service;

import com.garagehub.dto.ServiceRecordRequestDto;
import com.garagehub.dto.ServiceRecordResponseDto;
import com.garagehub.dto.ServiceReminderResponseDto;
import java.util.Optional;
import java.time.LocalDate;
import com.garagehub.entity.ServiceRecord;
import com.garagehub.entity.User;
import com.garagehub.entity.Vehicle;
import com.garagehub.enums.Role;
import com.garagehub.exception.AccessDeniedException;
import com.garagehub.exception.NotAMechanicException;
import com.garagehub.exception.ResourceNotFoundException;
import com.garagehub.mapper.ServiceRecordMapper;
import com.garagehub.repository.ServiceRecordRepository;
import com.garagehub.repository.UserRepository;
import com.garagehub.repository.VehicleRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<ServiceRecordResponseDto> getByVehicleId(Long vehicleId) {
        vehicleRepository.findById(vehicleId).orElseThrow(() -> new ResourceNotFoundException("Fordon hittades inte"));
        return serviceRecordRepository.findByVehicleId(vehicleId).stream().map(serviceRecordMapper::toResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ServiceRecordResponseDto> getAll() {
        User current = getCurrentUser();

        List<ServiceRecord> records;
        if (isPrivileged(current)) {
            records = serviceRecordRepository.findAll();
        } else {
            records = serviceRecordRepository.findByVehicleOwnerId(current.getId());
        }

        return records.stream().map(serviceRecordMapper::toResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public ServiceRecordResponseDto getById(Long id) {
        ServiceRecord serviceRecord = serviceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceRecord hittades inte"));

        User current = getCurrentUser();
        if (!isPrivileged(current) && !serviceRecord.getVehicle().getOwner().getId().equals(current.getId())) {
            throw new AccessDeniedException("Du har inte behörighet att se denna servicepost");
        }

        return serviceRecordMapper.toResponseDto(serviceRecord);
    }

    public ServiceRecordResponseDto create(ServiceRecordRequestDto dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Fordon hittades inte"));

        User current = getCurrentUser();
        if (!isPrivileged(current) && !vehicle.getOwner().getId().equals(current.getId())) {
            throw new AccessDeniedException("Du kan bara lägga till service för dina egna fordon");
        }

        User mechanic = null;
        if (dto.getMechanicId() != null) {
            mechanic = userRepository.findById(dto.getMechanicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mekaniker hittades inte"));
            if (mechanic.getRole() != Role.MECHANIC) {
                throw new NotAMechanicException("Användaren är inte en mekaniker");
            }
        }

        ServiceRecord serviceRecord = serviceRecordMapper.toEntity(dto, vehicle, mechanic);
        ServiceRecord saved = serviceRecordRepository.save(serviceRecord);
        return serviceRecordMapper.toResponseDto(saved);
    }



    public void delete(Long id) {
        ServiceRecord serviceRecord = serviceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceRecord hittades inte"));

        User current = getCurrentUser();
        boolean isOwner = serviceRecord.getVehicle().getOwner().getId().equals(current.getId());
        if (current.getRole() != Role.ADMIN && !isOwner) {
            throw new AccessDeniedException("Du kan bara ta bort dina egna serviceposter");
        }

        serviceRecordRepository.deleteById(id);
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