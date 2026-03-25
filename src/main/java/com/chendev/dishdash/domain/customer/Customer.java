package com.chendev.dishdash.domain.customer;

import com.chendev.dishdash.common.audit.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Unique login identifier. Enforced at DB level (UNIQUE constraint).
    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(name = "display_name", length = 128)
    private String displayName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    //0 = unspecified, 1 = male, 2 = female.
    @Column
    private Integer gender;

    @Column
    private LocalDate dob;
}
