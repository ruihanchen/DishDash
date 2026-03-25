package com.chendev.dishdash.domain.address.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
public class AddressRequest {

    @Size(max = 64, message = "Label must not exceed 64 characters")
    private String label;

    @NotBlank(message = "Recipient name is required")
    @Size(max = 128)
    private String recipient;

    @NotBlank(message = "Phone is required")
    @Size(max = 20)
    private String phone;

    @NotBlank(message = "Street address is required")
    @Size(max = 255)
    private String streetLine1;

    @Size(max = 255)
    private String streetLine2;

    @NotBlank(message = "City is required")
    @Size(max = 128)
    private String city;

    @NotBlank(message = "State is required")
    @Pattern(regexp = "[A-Z]{2}", message = "State must be a 2-letter US state code")
    private String state;

    @NotBlank(message = "ZIP code is required")
    @Pattern(regexp = "\\d{5}(-\\d{4})?", message = "ZIP code must be in format 12345 or 12345-6789")
    private String zipCode;

    private Boolean isDefault;
}
