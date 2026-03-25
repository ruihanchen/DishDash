package com.chendev.dishdash.domain.address.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressResponse {
    private Long id;
    private String label;
    private String recipient;
    private String phone;
    private String streetLine1;
    private String streetLine2;
    private String city;
    private String state;
    private String zipCode;
    private Boolean isDefault;
}
