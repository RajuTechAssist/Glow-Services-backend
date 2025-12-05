package com.glowservices.glow_services_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    @Lazy
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Glow Services - Your Verification Code");
            message.setText("Hello,\n\nYour One-Time Password (OTP) is: " + otp + "\n\nThis code expires in 5 minutes.");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("❌ Failed to send OTP email: " + e.getMessage());
        }
    }
    // ✅ NEW: Send Login Credentials (Only for new accounts)
    public void sendLoginCredentials(String toEmail, String name, String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Welcome to Glow Services! 🌸");
            message.setText("Hi " + name + ",\n\n" +
                    "Your booking has been confirmed! We have created an account for you.\n\n" +
                    "🔑 Username: " + toEmail + "\n" +
                    "🔑 Password: " + password + "\n\n" +
                    "Login here: https://glow-service.studio/customer/login\n\n" +
                    "Stay Glowing,\nGlow Services Team");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("❌ Failed to send credentials: " + e.getMessage());
        }
    }
    
    // ✅ NEW: Send Booking Confirmation (For everyone)
    public void sendBookingConfirmation(String toEmail, String name, String serviceName, String date, String time) {
         try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Booking Confirmed: " + serviceName);
            message.setText("Hi " + name + ",\n\n" +
                    "Your appointment for " + serviceName + " is confirmed!\n\n" +
                    "📅 Date: " + date + "\n" +
                    "⏰ Time: " + time + "\n\n" +
                    "Our expert will arrive at your location.\n\n" +
                    "Glow Services Team");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("❌ Failed to send booking confirmation: " + e.getMessage());
        }
    }
}