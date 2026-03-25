package com.chendev.dishdash.domain.auth;

import com.chendev.dishdash.common.response.ApiResponse;
import com.chendev.dishdash.domain.auth.dto.CustomerLoginResponse;
import com.chendev.dishdash.domain.auth.dto.SendOtpRequest;
import com.chendev.dishdash.domain.auth.dto.StaffLoginRequest;
import com.chendev.dishdash.domain.auth.dto.StaffLoginResponse;
import com.chendev.dishdash.domain.auth.dto.VerifyOtpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;
    private final CustomerAuthService customerAuthService;

    // Staff

    @PostMapping("/staff/login")
    @Operation(summary = "Staff login — returns JWT on success")
    public ResponseEntity<ApiResponse<StaffLoginResponse>> staffLogin(
            @Valid @RequestBody StaffLoginRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(authService.staffLogin(request)));
    }

    // Customer

    @PostMapping("/customer/send-otp")
    @Operation(summary = "Send OTP to customer phone",
            description = "Local dev: always sends 123456, no real SMS")
    public ResponseEntity<ApiResponse<String>> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {
        String otp = customerAuthService.sendOtp(request);


        // In local dev: return the OTP directly so testers don't need an SMS.
        return ResponseEntity.ok(ApiResponse.success(otp, "OTP sent (dev mode: code returned in response)"));
    }

    @PostMapping("/customer/verify-otp")
    @Operation(summary = "Verify OTP and receive JWT")
    public ResponseEntity<ApiResponse<CustomerLoginResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(customerAuthService.verifyOtp(request)));
    }
}
