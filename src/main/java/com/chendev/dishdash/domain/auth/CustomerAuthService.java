package com.chendev.dishdash.domain.auth;

import com.chendev.dishdash.common.exception.BusinessException;
import com.chendev.dishdash.common.exception.ErrorCode;
import com.chendev.dishdash.domain.auth.dto.CustomerLoginResponse;
import com.chendev.dishdash.domain.auth.dto.SendOtpRequest;
import com.chendev.dishdash.domain.auth.dto.VerifyOtpRequest;
import com.chendev.dishdash.domain.customer.Customer;
import com.chendev.dishdash.domain.customer.CustomerRepository;
import com.chendev.dishdash.infrastructure.security.JwtUtil;
import com.chendev.dishdash.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerAuthService {

    private final OtpService otpService;
    private final CustomerRepository customerRepository;
    private final JwtUtil jwtUtil;

    //Step 1: send OTP to the given phone number.

    public String sendOtp(SendOtpRequest request) {
        String otp = otpService.generateAndStore(request.getPhone());
        log.info("OTP send requested for phone: {}",
                maskPhone(request.getPhone()));

        // In production: call SmsService.send(request.getPhone(), otp)
        // and return null or a generic message — never return the OTP.
        return otp;
    }

    //Step 2: verify OTP and return a JWT.
    @Transactional
    public CustomerLoginResponse verifyOtp(VerifyOtpRequest request) {
        boolean valid = otpService.verify(request.getPhone(), request.getOtp());

        if (!valid) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS,
                    "Invalid or expired OTP. Please request a new one.");
        }

        // Upsert: find existing customer or create a new one
        boolean isNewUser = !customerRepository.existsByPhone(request.getPhone());

        Customer customer = customerRepository.findByPhone(request.getPhone())
                .orElseGet(() -> {
                    Customer newCustomer = Customer.builder()
                            .phone(request.getPhone())
                            .build();
                    Customer saved = customerRepository.save(newCustomer);
                    log.info("New customer registered: id={}", saved.getId());
                    return saved;
                });

        UserPrincipal principal = UserPrincipal.of(
                customer.getId(),
                customer.getPhone(),
                null,
                "ROLE_CUSTOMER"
        );

        String token = jwtUtil.generateToken(principal);

        return CustomerLoginResponse.builder()
                .id(customer.getId())
                .phone(customer.getPhone())
                .displayName(customer.getDisplayName())
                .token(token)
                .isNewUser(isNewUser)
                .build();
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "***";
        return phone.substring(0, Math.min(3, phone.length() - 4))
                + "***"
                + phone.substring(phone.length() - 4);
    }
}
