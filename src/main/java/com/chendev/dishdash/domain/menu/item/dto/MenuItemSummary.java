package com.chendev.dishdash.domain.menu.item.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;


@Getter
@Builder
public class MenuItemSummary {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private String imageUrl;
    private Integer status;
    private Integer sortOrder;
}
