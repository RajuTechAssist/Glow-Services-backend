package com.glowservices.glow_services_backend.controller;

import com.glowservices.glow_services_backend.model.dto.request.BookingRequest;
import com.glowservices.glow_services_backend.model.entity.Booking;
import com.glowservices.glow_services_backend.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
            Booking booking = bookingService.createBooking(request);
            return ResponseEntity.ok(Map.of(
                "message", "Booking successful!",
                "bookingId", booking.getId()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Booking updatedBooking = bookingService.updateBookingStatus(id, status);
            return ResponseEntity.ok(Map.of(
                "message", "Booking status updated successfully!",
                "bookingId", updatedBooking.getId(),
                "newStatus", updatedBooking.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}