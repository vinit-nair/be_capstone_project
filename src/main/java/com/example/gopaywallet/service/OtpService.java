package com.example.gopaywallet.service;

import com.example.gopaywallet.exception.InvalidOtpException;
import com.example.gopaywallet.exception.OtpExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private final ConcurrentHashMap<String, OtpData> otpMap = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    @Value("${otp.expiry.minutes}")
    private int otpExpiryMinutes;

    public String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1000000));
        otpMap.put(email, new OtpData(otp, LocalDateTime.now()));
        logger.info("generate Otp ::: {}", otpMap);
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        logger.info("OTPDATA::: {}", otpMap);
        OtpData otpData = otpMap.get(email);
        logger.info("OtpServbice::: validateOTP::: {}", otpData);
        if (otpData == null) {
            throw new InvalidOtpException("INVALID_OTP");
        }

        if (LocalDateTime.now().minusMinutes(otpExpiryMinutes)
                .isAfter(otpData.timestamp)) {
            otpMap.remove(email);
            throw new OtpExpiredException("OTP_EXPIRED");
        }

        if (!otpData.otp.equals(otp)) {
            throw new InvalidOtpException("INVALID_OTP");
        }

        //otpMap.remove(email); // Remove OTP after successful validation
        return true;
    }

    public void removeOtpDataForUser(String email) {
        OtpData otpData = otpMap.get(email);
        logger.info("OtpServbice::: validateOTP::: {}", otpData);
        otpMap.remove(email); // Remove OTP after successful validation
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
