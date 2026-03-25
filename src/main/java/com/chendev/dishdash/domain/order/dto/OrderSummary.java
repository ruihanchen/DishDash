package com.chendev.dishdash.domain.order.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
public class OrderSummary {
    private Long id;
    private String orderNumber;
    private Integer status;
    private BigDecimal amount;
    private Integer itemCount;
    private Instant createdAt;
}
