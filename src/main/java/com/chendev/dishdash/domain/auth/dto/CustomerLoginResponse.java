package com.chendev.dishdash.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class CustomerLoginResponse {

    private Long id;
    private String phone;
    private String displayName;
    private String token;
    private Boolean isNewUser;
}
