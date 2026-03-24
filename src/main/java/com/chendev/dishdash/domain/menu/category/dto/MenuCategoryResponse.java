package com.chendev.dishdash.domain.menu.category.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;


@Getter
@Builder
public class MenuCategoryResponse {

    private Long id;
    private String name;
    private Integer type;
    private Integer sortOrder;
    private Instant createdAt;
    private Instant updatedAt;
}
