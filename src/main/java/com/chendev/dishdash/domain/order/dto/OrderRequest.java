package com.chendev.dishdash.domain.order.dto;

import lombok.Getter;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Getter
public class OrderRequest {

    @NotNull(message = "Delivery address is required")
    private Long addressId;

    @NotNull(message = "Payment method is required")
    @Min(value = 1, message = "Payment method must be 1 (card) or 2 (cash)")
    @Max(value = 2, message = "Payment method must be 1 (card) or 2 (cash)")
    private Integer paymentMethod;

    private String note;
}

