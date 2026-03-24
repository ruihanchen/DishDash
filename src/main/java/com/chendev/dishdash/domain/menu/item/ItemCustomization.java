package com.chendev.dishdash.domain.menu.item;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "item_customizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCustomization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Back-reference to the owning MenuItem.insertable/updatable = false because the FK is managed by MenuItem's

    @Column(name = "item_id", nullable = false,
            insertable = false, updatable = false)
    private Long itemId;

    @Column(name = "option_name", nullable = false, length = 64)
    private String optionName;

    //JSON array stored as TEXT.

    @Column(nullable = false, columnDefinition = "TEXT")
    private String choices;
}
