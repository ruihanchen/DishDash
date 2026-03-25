package com.chendev.dishdash.domain.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Integer status;
    private BigDecimal amount;
    private BigDecimal deliveryFee;
    private Integer paymentMethod;
    private Integer paymentStatus;
    private String note;
    private String rejectionReason;
    private Instant estimatedDeliveryAt;
    private Instant createdAt;
    private List<LineItemResponse> lineItems;

    @Getter
    @Builder
    public static class LineItemResponse {
        private Long id;
        private String itemName;
        private String imageUrl;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subtotal;
    }
}
