package com.chendev.dishdash.domain.auth;

import com.chendev.dishdash.common.exception.BusinessException;
import com.chendev.dishdash.common.exception.ErrorCode;
import com.chendev.dishdash.domain.auth.dto.StaffLoginRequest;
import com.chendev.dishdash.domain.auth.dto.StaffLoginResponse;
import com.chendev.dishdash.domain.employee.Employee;
import com.chendev.dishdash.domain.employee.EmployeeRepository;
import com.chendev.dishdash.infrastructure.security.JwtUtil;
import com.chendev.dishdash.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeDetailsService employeeDetailsService;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public StaffLoginResponse staffLogin(StaffLoginRequest request) {

        // Step 1: load account
        // UsernameNotFoundException from loadUserByUsername is intentionally
        // caught and re-thrown as INVALID_CREDENTIALS — same error whether
        // the username doesn't exist or the password is wrong.
        UserDetails userDetails;
        try {
            userDetails = employeeDetailsService.loadUserByUsername(request.getUsername());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        // Step 2: verify password
        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        UserPrincipal principal = (UserPrincipal) userDetails;

        // Step 3: check account status and issue token
        Employee employee = employeeRepository
                .findActiveByUsername(principal.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        if (employee.getStatus() != 1) {
            log.warn("Suspended employee attempted login: id={}", employee.getId());
            throw new BusinessException(ErrorCode.ACCOUNT_SUSPENDED);
        }

        String token = jwtUtil.generateToken(principal);
        log.info("Staff login successful: id={}", employee.getId());

        return StaffLoginResponse.builder()
                .id(employee.getId())
                .username(employee.getUsername())
                .fullName(employee.getFullName())
                .token(token)
                .build();
    }
}