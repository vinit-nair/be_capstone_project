package com.example.gopaywallet.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private final ConcurrentHashMap<String, OtpData> otpMap = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    @Value("${otp.expiry.minutes}")
    private int otpExpiryMinutes;

    public String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1000000));
        otpMap.put(email, new OtpData(otp, LocalDateTime.now()));
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        OtpData otpData = otpMap.get(email);
        if (otpData == null) {
            return false;
        }

        boolean isValid = otpData.otp.equals(otp) &&
                LocalDateTime.now().minusMinutes(otpExpiryMinutes).isBefore(otpData.timestamp);

        if (isValid) {
            otpMap.remove(email); // Remove OTP after successful validation
        }

        return isValid;
    }

    private static class OtpData {
        final String otp;
        final LocalDateTime timestamp;

        OtpData(String otp, LocalDateTime timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }
}
