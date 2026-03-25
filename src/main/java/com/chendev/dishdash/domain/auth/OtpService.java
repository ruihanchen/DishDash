package com.chendev.dishdash.domain.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private static final String OTP_KEY_PREFIX = "otp:";
    private static final Duration OTP_TTL = Duration.ofMinutes(5);

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Generates and stores an OTP for the given phone number.
     * In the local profile, always returns "123456" so we
     * can test without a real SMS provider
     */
    public String generateAndStore(String phone) {
        String otp = generateOtp();
        redisTemplate.opsForValue().set(otpKey(phone), otp, OTP_TTL);
        log.debug("OTP stored for phone: {}. Expires in {} minutes",
                maskPhone(phone), OTP_TTL.toMinutes());
        return otp;
    }

    //Deletes the OTP on successful verification to prevent reuse.
    public boolean verify(String phone, String submittedOtp) {
        String key = otpKey(phone);
        Object stored = redisTemplate.opsForValue().get(key);

        if (stored == null) {
            log.debug("OTP verification failed: no OTP found for phone {}", maskPhone(phone));
            return false;
        }

        boolean matches = submittedOtp.equals(stored.toString());

        if (matches) {
            // Delete immediately after successful verification.
            // Leaving it in Redis would allow reuse until TTL expires.
            redisTemplate.delete(key);
            log.debug("OTP verified and consumed for phone: {}", maskPhone(phone));
        } else {
            log.debug("OTP mismatch for phone: {}", maskPhone(phone));
        }

        return matches;
    }

    //Fixed OTP for local development.

    private String generateOtp() {
        return "123456";
    }

    private String otpKey(String phone) {
        return OTP_KEY_PREFIX + phone;
    }

    //Masks phone for logging — never log full phone numbers.
    // such as: "4155551234" → "415***1234"
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "***";
        return phone.substring(0, Math.min(3, phone.length() - 4))
                + "***"
                + phone.substring(phone.length() - 4);
    }
}
