package com.glowservices.glow_services_backend.model.dto.request;

import lombok.Data;

@Data
public class BookingRequest {
    private Long serviceId;
    private Integer quantity;
    private String selectedDate;
    private String selectedTime;
    private String serviceLocation;
    private String address;
    private String city;
    private String pincode;
    
    // Customer Info (Used if guest)
    private String fullName;
    private String email;
    private String phone;
    private String specialInstructions;
}