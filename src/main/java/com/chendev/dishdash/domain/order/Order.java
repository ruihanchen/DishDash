package com.chendev.dishdash.domain.order;

import com.chendev.dishdash.common.audit.BaseEntity;
import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 64)
    private String orderNumber;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "address_id")
    private Long addressId;

    /**
     * 1=pending, 2=confirmed, 3=preparing,
     * 4=out_for_delivery, 5=delivered, 6=cancelled
     */
    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "delivery_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    /** 1 = card, 2 = cash */
    @Column(name = "payment_method", nullable = false)
    private Integer paymentMethod;

    /** 0 = unpaid, 1 = paid */
    @Column(name = "payment_status", nullable = false)
    private Integer paymentStatus;

    @Column(name = "estimated_delivery_at")
    private Instant estimatedDeliveryAt;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    @Builder.Default
    private List<OrderLineItem> lineItems = new ArrayList<>();
}
