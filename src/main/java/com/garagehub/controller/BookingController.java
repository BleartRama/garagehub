package com.garagehub.controller;

import com.garagehub.dto.BookingRequestDto;
import com.garagehub.dto.BookingResponseDto;
import com.garagehub.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAll() {
        return ResponseEntity.ok(bookingService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getById(id));
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> save(@Valid @RequestBody BookingRequestDto dto) {
        return ResponseEntity.status(201).body(bookingService.create(dto));
    }



    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('MECHANIC') or hasRole('ADMIN')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<BookingResponseDto> approve(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.approve(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDto> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancel(id));
    }
}