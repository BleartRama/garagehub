package com.garagehub.controller;

import com.garagehub.dto.BookingRequestDto;
import com.garagehub.dto.BookingResponseDto;
import com.garagehub.service.BookingService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BookingResponseDto> save(@RequestBody BookingRequestDto dto) {
        return ResponseEntity.status(201).body(bookingService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponseDto> update(@PathVariable Long id, @RequestBody BookingRequestDto dto) {
        return ResponseEntity.ok(bookingService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}