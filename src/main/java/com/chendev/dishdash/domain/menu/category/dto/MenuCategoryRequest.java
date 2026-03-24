package com.chendev.dishdash.domain.menu.category.dto;

import lombok.Getter;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class MenuCategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 128, message = "Category name must not exceed 128 characters")
    private String name;

    @NotNull(message = "Category type is required")
    @Min(value = 1, message = "Type must be 1 (dish) or 2 (combo)")
    @Max(value = 2, message = "Type must be 1 (dish) or 2 (combo)")
    private Integer type;

    @NotNull(message = "Sort order is required")
    private Integer sortOrder;
}
