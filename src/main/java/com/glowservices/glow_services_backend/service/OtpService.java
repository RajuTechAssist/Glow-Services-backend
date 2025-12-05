package com.glowservices.glow_services_backend.service;

import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    // Simple in-memory cache. For production scaling, use Redis.
    // Key: Email/Phone, Value: OtpData
    private final ConcurrentHashMap<String, OtpData> otpCache = new ConcurrentHashMap<>();
    private final long OTP_VALID_DURATION = TimeUnit.MINUTES.toMillis(5); // 5 Minutes

    public String generateOtp(String identifier) {
        // Generate 6-digit Code
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Store in cache
        otpCache.put(identifier, new OtpData(otp, System.currentTimeMillis()));
        return otp;
    }

    public boolean validateOtp(String identifier, String otp) {
        OtpData data = otpCache.get(identifier);
        
        if (data == null) return false; // No OTP found

        // Check expiry
        if (System.currentTimeMillis() - data.timestamp > OTP_VALID_DURATION) {
            otpCache.remove(identifier);
            return false;
        }

        // Check match
        if (data.otp.equals(otp)) {
            otpCache.remove(identifier); // Clear OTP after successful use
            return true;
        }
        return false;
    }

    // Helper class to store OTP + Timestamp
    private static class OtpData {
        String otp;
        long timestamp;
        public OtpData(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }
}