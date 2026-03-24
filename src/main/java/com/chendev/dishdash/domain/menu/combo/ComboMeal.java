package com.chendev.dishdash.domain.menu.combo;

import com.chendev.dishdash.common.audit.BaseEntity;
import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "combo_meals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComboMeal extends BaseEntity {

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

    @Column(nullable = false)
    private Integer status;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "combo_id")
    @Builder.Default
    private List<ComboMealItem> items = new ArrayList<>();
}
