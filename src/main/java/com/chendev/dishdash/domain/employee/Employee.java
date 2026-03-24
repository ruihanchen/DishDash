package com.chendev.dishdash.domain.employee;

import com.chendev.dishdash.common.audit.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @Column(name = "full_name", nullable = false, length = 128)
    private String fullName;

    //bcrypt hash. Never log, serialize
    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 20)
    private String phone;

    @Column(name = "avatar_url")
    private String avatarUrl;

    //1 = active, 0 = suspended.
    @Column(nullable = false)
    private Integer status;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
