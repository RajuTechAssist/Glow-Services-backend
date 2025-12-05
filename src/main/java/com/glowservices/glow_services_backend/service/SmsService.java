package com.glowservices.glow_services_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

@Service
public class SmsService {

    @Autowired
    private SnsClient snsClient;

    public boolean sendOtpSms(String phoneNumber, String otp) {
        String message = "Your Glow Services Verification Code is: " + otp;

        // Ensure phone number has country code (e.g., +91 for India)
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+91" + phoneNumber; // Default to India if missing
        }

        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(phoneNumber)
                    .build();

            PublishResponse result = snsClient.publish(request);
            System.out.println("✅ SMS sent successfully. Message ID: " + result.messageId());
            return true;

        } catch (SnsException e) {
            System.err.println("❌ AWS SNS Error: " + e.awsErrorDetails().errorMessage());
            return false;
        }
    }
}