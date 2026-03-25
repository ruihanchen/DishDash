package com.chendev.dishdash.domain.address;

import com.chendev.dishdash.common.audit.BaseEntity;
import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "delivery_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /** e.g., "Home", "Work", "Other" — optional display label. */
    @Column(length = 64)
    private String label;

    @Column(nullable = false, length = 128)
    private String recipient;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "street_line1", nullable = false, length = 255)
    private String streetLine1;

    @Column(name = "street_line2", length = 255)
    private String streetLine2;

    @Column(nullable = false, length = 128)
    private String city;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(name = "zip_code", nullable = false, length = 10)
    private String zipCode;

    //Only one address per customer can be default.
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;
}