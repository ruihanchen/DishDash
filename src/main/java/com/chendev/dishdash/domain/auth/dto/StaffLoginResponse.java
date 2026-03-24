package com.chendev.dishdash.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffLoginResponse {

    private Long id;
    private String username;
    private String fullName;
    private String token;
}
