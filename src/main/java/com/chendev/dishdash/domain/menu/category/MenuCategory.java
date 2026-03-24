package com.chendev.dishdash.domain.menu.category;

import com.chendev.dishdash.common.audit.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "menu_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String name;

    // 1 = dish category, 2 = combo category.
    @Column(nullable = false)
    private Integer type;

    //Display order — lower value appears first. Defaults to 0; staff can reorder via the management UI.

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
