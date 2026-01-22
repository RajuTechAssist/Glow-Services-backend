package com.glowservices.glow_services_backend.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.glowservices.glow_services_backend.model.entity.Booking;
import com.glowservices.glow_services_backend.model.entity.Customer;
import com.glowservices.glow_services_backend.model.enums.BookingStatus;
import com.glowservices.glow_services_backend.repository.BookingRepository;
import com.glowservices.glow_services_backend.repository.CustomerRepository;

@RestController
@RequestMapping("/api/customers")
public class CustomerPortalController {

    @Autowired private CustomerRepository customerRepository;
    @Autowired private BookingRepository bookingRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        Customer customer = requireCustomer(id);
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody Customer payload) {
        Customer existing = requireCustomer(id);

        // Update only provided fields to avoid clobbering existing data with nulls
        if (payload.getFullName() != null) existing.setFullName(payload.getFullName());
        if (payload.getEmail() != null) existing.setEmail(payload.getEmail());
        if (payload.getPhone() != null) existing.setPhone(payload.getPhone());
        if (payload.getAddress() != null) existing.setAddress(payload.getAddress());
        if (payload.getCity() != null) existing.setCity(payload.getCity());
        if (payload.getState() != null) existing.setState(payload.getState());
        if (payload.getPincode() != null) existing.setPincode(payload.getPincode());
        if (payload.getDateOfBirth() != null) existing.setDateOfBirth(payload.getDateOfBirth());
        if (payload.getGender() != null) existing.setGender(payload.getGender());
        if (payload.getPreferredServiceLocation() != null) existing.setPreferredServiceLocation(payload.getPreferredServiceLocation());
        if (payload.getSpecialInstructions() != null) existing.setSpecialInstructions(payload.getSpecialInstructions());
        if (payload.getStatus() != null) existing.setStatus(payload.getStatus());

        Customer saved = customerRepository.save(existing);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}/dashboard")
    public ResponseEntity<?> getDashboard(@PathVariable Long id) {
        Customer customer = requireCustomer(id);
        List<Booking> bookings = bookingRepository.findByCustomerId(id);

        double totalSpent = bookings.stream()
                .mapToDouble(b -> Optional.ofNullable(b.getTotalPrice()).orElse(0.0))
                .sum();

        long servicesCompleted = bookings.stream()
                .filter(b -> BookingStatus.COMPLETED.equals(b.getStatus()))
                .count();

        List<Map<String, Object>> upcoming = bookings.stream()
                .filter(b -> !BookingStatus.CANCELLED.equals(b.getStatus()))
                .filter(b -> isOnOrAfterToday(b.getBookingDate()))
                .sorted(Comparator.comparing(Booking::getBookingDate))
                .limit(5)
                .map(b -> Map.<String, Object>of(
                        "id", b.getId(),
                        "service", b.getService() != null ? b.getService().getName() : "",
                        "date", b.getBookingDate(),
                        "time", b.getBookingTime(),
                        "status", b.getStatus() != null ? b.getStatus().name().toLowerCase() : "pending"
                ))
                .toList();

        List<Map<String, Object>> recentOrders = bookings.stream()
                .sorted(Comparator.comparing(Booking::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(5)
                .map(b -> Map.of(
                        "id", b.getId(),
                        "date", b.getCreatedAt(),
                        "items", List.of(b.getService() != null ? b.getService().getName() : ""),
                        "total", b.getTotalPrice(),
                        "status", b.getStatus() != null ? b.getStatus().name().toLowerCase() : "pending"
                ))
                .toList();

        List<String> favoriteServices = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getService() != null ? b.getService().getName() : "", Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        int points = Optional.ofNullable(customer.getLoyaltyPoints()).orElse(0);
        Map<String, Object> body = Map.of(
                "loyaltyPoints", points,
                "tier", calculateTier(points),
                "totalSpent", totalSpent,
                "servicesCompleted", servicesCompleted,
                "favoriteServices", favoriteServices,
                "upcomingBookings", upcoming,
                "recentOrders", recentOrders,
                "memberSince", customer.getCreatedAt()
        );

        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<?> getOrders(@PathVariable Long id, @RequestParam(defaultValue = "all") String status) {
        requireCustomer(id);
        List<Booking> bookings = bookingRepository.findByCustomerId(id);

        List<Map<String, Object>> response = bookings.stream()
                .filter(b -> "all".equalsIgnoreCase(status) || (b.getStatus() != null && b.getStatus().name().equalsIgnoreCase(status)))
                .sorted(Comparator.comparing(Booking::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(b -> Map.<String, Object>ofEntries(
                        Map.entry("id", b.getId()),
                        Map.entry("date", b.getCreatedAt()),
                        Map.entry("services", List.of(b.getService() != null ? b.getService().getName() : "")),
                        Map.entry("total", b.getTotalPrice()),
                        Map.entry("status", b.getStatus() != null ? b.getStatus().name().toLowerCase() : "pending"),
                        Map.entry("bookingDate", b.getBookingDate()),
                        Map.entry("bookingTime", b.getBookingTime()),
                        Map.entry("location", b.getServiceLocation()),
                        Map.entry("address", b.getAddress()),
                        Map.entry("city", b.getCity()),
                        Map.entry("pincode", b.getPincode())
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<?> getHistory(@PathVariable Long id) {
        requireCustomer(id);
        List<Map<String, Object>> completed = bookingRepository.findByCustomerId(id).stream()
                .filter(b -> BookingStatus.COMPLETED.equals(b.getStatus()))
                .sorted(Comparator.comparing(Booking::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(b -> Map.<String, Object>of(
                        "id", b.getId(),
                        "service", b.getService() != null ? b.getService().getName() : "",
                        "date", b.getBookingDate(),
                        "time", b.getBookingTime(),
                        "total", b.getTotalPrice()
                ))
                .toList();
        return ResponseEntity.ok(completed);
    }

    @GetMapping("/{id}/rewards")
    public ResponseEntity<?> getRewards(@PathVariable Long id) {
        Customer customer = requireCustomer(id);
        int points = Optional.ofNullable(customer.getLoyaltyPoints()).orElse(0);
        String tier = calculateTier(points);
        int nextTierPoints = tier.equals("Platinum") ? points : nextTierThreshold(points) - points;

        Map<String, Object> body = Map.of(
                "points", points,
                "tier", tier,
                "nextTierPoints", nextTierThreshold(points),
                "redemptions", List.of(
                        Map.of("id", 1, "name", "₹100 Off", "cost", 500, "redeemed", false),
                        Map.of("id", 2, "name", "Free Manicure", "cost", 800, "redeemed", false),
                        Map.of("id", 3, "name", "Exclusive Gift Kit", "cost", 1500, "redeemed", points >= 1500)
                ),
                "history", List.of()
        );
        return ResponseEntity.ok(body);
    }

    private Customer requireCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));
    }

    private boolean isOnOrAfterToday(String date) {
        if (date == null) return false;
        try {
            LocalDate bookingDate = LocalDate.parse(date, DATE_FMT);
            return !bookingDate.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private String calculateTier(int points) {
        if (points >= 1500) return "Platinum";
        if (points >= 800) return "Gold";
        if (points >= 300) return "Silver";
        return "Basic";
    }

    private int nextTierThreshold(int points) {
        if (points >= 1500) return points; // already top tier
        if (points >= 800) return 1500;
        if (points >= 300) return 800;
        return 300;
    }
}
