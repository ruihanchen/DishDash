package com.chendev.dishdash.domain.auth;

import com.chendev.dishdash.domain.employee.Employee;
import com.chendev.dishdash.domain.employee.EmployeeRepository;
import com.chendev.dishdash.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findActiveByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No active employee: " + username));

        return UserPrincipal.of(
                employee.getId(),
                employee.getUsername(),
                employee.getPassword(),
                "ROLE_STAFF"
        );
    }
}