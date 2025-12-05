package com.glowservices.glow_services_backend.service;

import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    // Simulating a cache (In production, use Redis)
    private final ConcurrentHashMap<String, OtpData> otpCache = new ConcurrentHashMap<>();
    private final long OTP_VALID_DURATION = TimeUnit.MINUTES.toMillis(5);

    public String generateOtp(String identifier) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpCache.put(identifier, new OtpData(otp, System.currentTimeMillis()));
        return otp;
    }

    public boolean validateOtp(String identifier, String otp) {
        OtpData data = otpCache.get(identifier);
        if (data == null) return false;

        if (System.currentTimeMillis() - data.timestamp > OTP_VALID_DURATION) {
            otpCache.remove(identifier);
            return false;
        }

        if (data.otp.equals(otp)) {
            otpCache.remove(identifier); // Clear after success
            return true;
        }
        return false;
    }

    private static class OtpData {
        String otp;
        long timestamp;
        public OtpData(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }
}