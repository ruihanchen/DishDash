package com.chendev.dishdash.domain.auth;

import com.chendev.dishdash.common.response.ApiResponse;
import com.chendev.dishdash.domain.auth.dto.StaffLoginRequest;
import com.chendev.dishdash.domain.auth.dto.StaffLoginResponse;
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

    @PostMapping("/staff/login")
    @Operation(summary = "Staff login — returns JWT on success")
    public ResponseEntity<ApiResponse<StaffLoginResponse>> staffLogin(
            @Valid @RequestBody StaffLoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.staffLogin(request)));
    }
}
