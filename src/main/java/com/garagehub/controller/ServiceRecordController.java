package com.garagehub.controller;

import com.garagehub.dto.ServiceRecordRequestDto;
import com.garagehub.dto.ServiceRecordResponseDto;
import com.garagehub.service.ServiceRecordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-records")
public class ServiceRecordController {

    private final ServiceRecordService serviceRecordService;

    public ServiceRecordController(ServiceRecordService serviceRecordService) {
        this.serviceRecordService = serviceRecordService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceRecordResponseDto>> getAll() {
        return ResponseEntity.ok(serviceRecordService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRecordResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(serviceRecordService.getById(id));
    }

    @PreAuthorize("hasRole('MECHANIC') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ServiceRecordResponseDto> save(@Valid @RequestBody ServiceRecordRequestDto dto) {
        return ResponseEntity.status(201).body(serviceRecordService.create(dto));
    }

    @PreAuthorize("hasRole('MECHANIC') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceRecordResponseDto> update(@PathVariable Long id,@Valid @RequestBody ServiceRecordRequestDto dto) {
        return ResponseEntity.ok(serviceRecordService.update(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}