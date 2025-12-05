package com.glowservices.glow_services_backend.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glowservices.glow_services_backend.model.entity.Customer;
import com.glowservices.glow_services_backend.repository.CustomerRepository;
import com.glowservices.glow_services_backend.service.EmailService;
import com.glowservices.glow_services_backend.service.OtpService;
import com.glowservices.glow_services_backend.service.SmsService;

@RestController
@RequestMapping("/api/customers")
// @CrossOrigin(origins="http://localhost:5173", allowCredentials="true")
public class CustomerAuthController {

    @Autowired private CustomerRepository customerRepo;
    @Autowired private OtpService otpService;
    @Autowired private EmailService emailService;
    @Autowired private SmsService smsService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Customer customer) {
        if (customerRepo.findByEmail(customer.getEmail()).isPresent()) {
            return ResponseEntity.status(400)
               .body(Map.of("message","Email already in use"));
        }
        customer.setCreatedAt(LocalDateTime.now());
        Customer saved = customerRepo.save(customer);
        Map<String,Object> resp = new HashMap<>();
        resp.put("id", saved.getId());
        resp.put("fullName", saved.getFullName());
        resp.put("email", saved.getEmail());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String email = body.get("email");
        String password = body.get("password"); // In demo, we skip password or compare plain
        Optional<Customer> opt = customerRepo.findByEmail(email);
        if (opt.isPresent()) {
            Customer c = opt.get();
            // For demo, accept any password
            Map<String,Object> resp = new HashMap<>();
            resp.put("id",c.getId());
            resp.put("fullName",c.getFullName());
            resp.put("email",c.getEmail());
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.status(401).body(Map.of("message","Invalid credentials"));
    }


    // 1. Endpoint to SEND OTP
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String type = request.get("type"); // "email" or "phone"
        String identifier = request.get("identifier"); // actual email or phone number

        if (identifier == null || identifier.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Identifier is required"));
        }

        String otp = otpService.generateOtp(identifier);

        boolean sent = false;
        if ("email".equalsIgnoreCase(type)) {
            emailService.sendOtpEmail(identifier, otp);
            sent = true;
        } else if ("phone".equalsIgnoreCase(type)) {
            sent = smsService.sendOtpSms(identifier, otp);
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid type. Use 'email' or 'phone'"));
        }

        if (sent) {
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully", "status", "success"));
        } else {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to send OTP"));
        }
    }

    // 2. Endpoint to VERIFY OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String identifier = request.get("identifier");
        String otp = request.get("otp");

        boolean isValid = otpService.validateOtp(identifier, otp);

        if (isValid) {
            return ResponseEntity.ok(Map.of("status", "verified", "message", "OTP Verified"));
        } else {
            return ResponseEntity.status(400).body(Map.of("status", "failed", "message", "Invalid or Expired OTP"));
        }
    }
}
