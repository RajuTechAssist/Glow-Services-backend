package com.glowservices.glow_services_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@glowservices.com");
        message.setTo(toEmail);
        message.setSubject("Glow Services - Verification Code");
        message.setText("Your One-Time Password (OTP) is: " + otp + "\n\nThis code is valid for 5 minutes.");
        mailSender.send(message);
    }
    
    public void sendLoginCredentials(String toEmail, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Welcome to Glow Services!");
        message.setText("Your account has been created.\n\nUsername: " + toEmail + "\nPassword: " + password + "\n\nPlease login and change your password.");
        mailSender.send(message);
    }
}