package com.chendev.dishdash.domain.menu.item;

import com.chendev.dishdash.common.audit.BaseEntity;
import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** 1 = available, 0 = unavailable (hidden from customers). */
    @Column(nullable = false)
    private Integer status;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    /**
     * Customization options owned by this item.
     * CascadeType.ALL: persisting/deleting the item cascades to its customizations.
     * orphanRemoval = true: removing a customization from this list deletes it from DB.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    @Builder.Default
    private List<ItemCustomization> customizations = new ArrayList<>();
}
