package com.garagehub.controller;

import com.garagehub.dto.ServiceRecordResponseDto;
import com.garagehub.dto.VehicleRequestDto;
import com.garagehub.dto.VehicleResponseDto;
import com.garagehub.service.ServiceRecordService;
import com.garagehub.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")

public class VehicleController {

    private final VehicleService vehicleService;
    private final ServiceRecordService serviceRecordService;




    public VehicleController(VehicleService vehicleService, ServiceRecordService serviceRecordService) {
        this.vehicleService = vehicleService;
        this.serviceRecordService = serviceRecordService;
    }

    @GetMapping("/{id}/service-records")
    public ResponseEntity<List<ServiceRecordResponseDto>> getByVehicleId(@PathVariable Long id) {
        return ResponseEntity.ok(serviceRecordService.getByVehicleId(id));
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponseDto>> getAll() {
        return ResponseEntity.ok(vehicleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getById(id));
    }

    @PostMapping
    public ResponseEntity<VehicleResponseDto> save(@Valid @RequestBody VehicleRequestDto dto) {
        return ResponseEntity.status(201).body(vehicleService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDto> update(@PathVariable Long id, @Valid @RequestBody VehicleRequestDto dto) {
        return ResponseEntity.ok(vehicleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

