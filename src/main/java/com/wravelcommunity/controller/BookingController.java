package com.wravelcommunity.controller;

import com.wravelcommunity.dto.*;
import com.wravelcommunity.model.Booking;
import com.wravelcommunity.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request, @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(bookingService.createBooking(request, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getUserBookings(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(bookingService.getUserBookings(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(bookingService.cancelBooking(id, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<Booking>> getAllBookings(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(bookingService.getAllBookings(token));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestBody StatusRequest request, @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(bookingService.updateBookingStatus(id, request.getStatus(), token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}
