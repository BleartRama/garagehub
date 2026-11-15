package com.garagehub.mapper;
import com.garagehub.dto.ServiceRecordResponseDto;
import com.garagehub.entity.ServiceRecord;
import com.garagehub.entity.User;
import com.garagehub.entity.Vehicle;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class ServiceRecordMapperTest {
    private final ServiceRecordMapper mapper = new ServiceRecordMapper();

    @Test
    void toResponseDto_nar_MekanikerFinns_satterFullstandigtNamn() {
        User mekaniker = new User("Anna", "Svensson", "anna@test.se");
        Vehicle fordon = new Vehicle("ABC123", "Volvo", "V70", 2015, 12000, null);
        ServiceRecord record = new ServiceRecord(
                LocalDate.now(), "Oljebyte", 1200.00, 15000, fordon, mekaniker);

        ServiceRecordResponseDto dto = mapper.toResponseDto(record);

        assertEquals("Anna Svensson", dto.getMechanicName());
        assertEquals("ABC123", dto.getVehicleRegistrationNumber());
        assertEquals("Oljebyte", dto.getDescription());
    }

    @Test
    void toResponseDto_narMekanikerSaknas_satterNamnetTillNull() {
        Vehicle fordon = new Vehicle("ABC123", "Volvo", "V70", 2015, 12000, null);
        ServiceRecord record = new ServiceRecord(
                LocalDate.now(), "Oljebyte", 1200.0, 15000, fordon, null);

        ServiceRecordResponseDto dto = mapper.toResponseDto(record);

        assertNull(dto.getMechanicName());
        assertEquals("ABC123", dto.getVehicleRegistrationNumber());
    }
}
