package com.glowservices.glow_services_backend.service;

import com.glowservices.glow_services_backend.model.dto.request.BookingRequest;
import com.glowservices.glow_services_backend.model.entity.Booking;
import com.glowservices.glow_services_backend.model.entity.Customer;
import com.glowservices.glow_services_backend.model.entity.ServiceEntity;
import com.glowservices.glow_services_backend.model.enums.BookingStatus;
import com.glowservices.glow_services_backend.repository.BookingRepository;
import com.glowservices.glow_services_backend.repository.CustomerRepository;
import com.glowservices.glow_services_backend.repository.ServiceRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private EmailService emailService;

    @Transactional
    public Booking createBooking(BookingRequest request) {
        // 1. Find Service
        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // 2. Get Existing or Create New Customer
        Customer customer = getOrCreateCustomer(request);

        // 3. Save Booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setService(service);
        booking.setQuantity(request.getQuantity());
        booking.setTotalPrice(service.getPrice() * request.getQuantity());
        booking.setBookingDate(request.getSelectedDate());
        booking.setBookingTime(request.getSelectedTime());
        booking.setServiceLocation(request.getServiceLocation());
        booking.setAddress(request.getAddress());
        booking.setCity(request.getCity());
        booking.setPincode(request.getPincode());
        booking.setSpecialInstructions(request.getSpecialInstructions());
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        // 4. Email Confirmation
        emailService.sendBookingConfirmation(customer.getEmail(), customer.getFullName(), service.getName(), request.getSelectedDate(), request.getSelectedTime());

        return savedBooking;
    }

    private Customer getOrCreateCustomer(BookingRequest request) {
        // ✅ LOGIC: Check if email exists
        Optional<Customer> existing = customerRepository.findByEmail(request.getEmail());

        if (existing.isPresent()) {
            // User exists -> Return them (Do NOT create new account)
            return existing.get();
        }

        // ✅ User does NOT exist -> Create Account & Send Password
        Customer newCustomer = new Customer();
        newCustomer.setFullName(request.getFullName());
        newCustomer.setEmail(request.getEmail());
        newCustomer.setPhone(request.getPhone());
        newCustomer.setAddress(request.getAddress());
        newCustomer.setCity(request.getCity());
        newCustomer.setPincode(request.getPincode());
        newCustomer.setCreatedAt(LocalDateTime.now());
        
        // Generate random password
        String tempPassword = RandomStringUtils.randomAlphanumeric(8);
        // In real app: newCustomer.setPassword(encoder.encode(tempPassword));
        
        Customer saved = customerRepository.save(newCustomer);

        // Email them the password
        emailService.sendLoginCredentials(request.getEmail(), request.getFullName(), tempPassword);

        return saved;
    }
}