package com.chendev.dishdash.domain.menu.combo.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class ComboMealResponse {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private String imageUrl;
    private String description;
    private Integer status;
    private List<ComboItemResponse> items;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter
    @Builder
    public static class ComboItemResponse {
        private Long itemId;
        private String itemName;
        private Integer quantity;
    }
}
