package com.glowservices.glow_services_backend.repository;

import com.glowservices.glow_services_backend.model.entity.Booking;
import com.glowservices.glow_services_backend.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Finds all bookings for a specific customer
    List<Booking> findByCustomerId(Long customerId);
    
    // Finds all bookings with a specific status (e.g., PENDING)
    List<Booking> findByStatus(BookingStatus status);
}