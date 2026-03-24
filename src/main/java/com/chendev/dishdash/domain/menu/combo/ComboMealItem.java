package com.chendev.dishdash.domain.menu.combo;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "combo_meal_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComboMealItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "combo_id", nullable = false,
            insertable = false, updatable = false)
    private Long comboId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private Integer quantity;
}