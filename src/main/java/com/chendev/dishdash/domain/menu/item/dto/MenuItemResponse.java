package com.chendev.dishdash.domain.menu.item.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Getter
@Builder
public class MenuItemResponse {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private String imageUrl;
    private String description;
    private Integer status;
    private Integer sortOrder;
    private List<CustomizationResponse> customizations;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter
    @Builder
    public static class CustomizationResponse {
        private Long id;
        private String optionName;
        private List<String> choices;
    }
}
