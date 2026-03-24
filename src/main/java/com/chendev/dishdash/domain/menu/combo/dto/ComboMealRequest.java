package com.chendev.dishdash.domain.menu.combo.dto;

import lombok.Getter;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
public class ComboMealRequest {

    @NotBlank(message = "Combo name is required")
    @Size(max = 128)
    private String name;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    private String imageUrl;
    private String description;

    @NotNull(message = "Status is required")
    @Min(0) @Max(1)
    private Integer status;

    @NotNull(message = "Items are required")
    @Size(min = 1, message = "A combo must contain at least one item")
    private List<ComboItemRequest> items;

    @Getter
    public static class ComboItemRequest {
        @NotNull(message = "Item ID is required")
        private Long itemId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }
}
