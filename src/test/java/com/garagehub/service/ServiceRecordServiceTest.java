package com.garagehub.service;

import com.garagehub.dto.ServiceReminderResponseDto;
import com.garagehub.entity.ServiceRecord;
import com.garagehub.entity.Vehicle;
import com.garagehub.mapper.ServiceRecordMapper;
import com.garagehub.repository.ServiceRecordRepository;
import com.garagehub.repository.UserRepository;
import com.garagehub.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceRecordServiceTest {

    private static final Long VEHICLE_ID = 1L;

    @Mock private ServiceRecordRepository serviceRecordRepository;
    @Mock private VehicleRepository vehicleRepository;
    @Mock private UserRepository userRepository;
    @Mock private ServiceRecordMapper serviceRecordMapper;

    @InjectMocks private ServiceRecordService service;

    private Vehicle vehicleWithMileage(int mileage) {
        return new Vehicle("ABC123", "Volvo", "V70", 2015, mileage, null);
    }

    private ServiceRecord recordWith(LocalDate serviceDate, int nextRecommendedMileage) {
        return new ServiceRecord(serviceDate, "Oil change", 1200.0,
                nextRecommendedMileage, vehicleWithMileage(0), null);
    }

    @Test
    void getServiceReminder_whenNoServiceRecordExists_returnsNotDue() {
        when(vehicleRepository.findById(VEHICLE_ID))
                .thenReturn(Optional.of(vehicleWithMileage(12000)));
        when(serviceRecordRepository.findTopByVehicleIdOrderByServiceDateDesc(VEHICLE_ID))
                .thenReturn(Optional.empty());

        ServiceReminderResponseDto dto = service.getServiceReminder(VEHICLE_ID);

        assertFalse(dto.isDueForService());
        assertTrue(dto.getStatus().contains("Ingen service registrerad"));
    }

    @Test
    void getServiceReminder_whenMileageReached_returnsDue() {
        when(vehicleRepository.findById(VEHICLE_ID))
                .thenReturn(Optional.of(vehicleWithMileage(20000)));
        when(serviceRecordRepository.findTopByVehicleIdOrderByServiceDateDesc(VEHICLE_ID))
                .thenReturn(Optional.of(recordWith(LocalDate.now(), 15000)));

        ServiceReminderResponseDto dto = service.getServiceReminder(VEHICLE_ID);

        assertTrue(dto.isDueForService());
        assertTrue(dto.getStatus().contains("Mätarställning passerad"));
    }

    @Test
    void getServiceReminder_whenLastServiceOlderThanTwelveMonths_returnsDue() {
        when(vehicleRepository.findById(VEHICLE_ID))
                .thenReturn(Optional.of(vehicleWithMileage(10000)));
        when(serviceRecordRepository.findTopByVehicleIdOrderByServiceDateDesc(VEHICLE_ID))
                .thenReturn(Optional.of(recordWith(LocalDate.now().minusMonths(13), 15000)));

        ServiceReminderResponseDto dto = service.getServiceReminder(VEHICLE_ID);

        assertTrue(dto.isDueForService());
        assertTrue(dto.getStatus().contains("12 månader"));
    }

    @Test
    void getServiceReminder_whenMileageNotReachedAndServiceIsRecent_returnsNotDue() {
        when(vehicleRepository.findById(VEHICLE_ID))
                .thenReturn(Optional.of(vehicleWithMileage(10000)));
        when(serviceRecordRepository.findTopByVehicleIdOrderByServiceDateDesc(VEHICLE_ID))
                .thenReturn(Optional.of(recordWith(LocalDate.now().minusMonths(2), 15000)));

        ServiceReminderResponseDto dto = service.getServiceReminder(VEHICLE_ID);

        assertFalse(dto.isDueForService());
        assertTrue(dto.getStatus().contains("Inte dags för service"));
    }
}
