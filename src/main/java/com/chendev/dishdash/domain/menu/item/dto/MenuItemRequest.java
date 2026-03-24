package com.chendev.dishdash.domain.menu.item.dto;

import lombok.Getter;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;


@Getter
public class MenuItemRequest {

    @NotBlank(message = "Item name is required")
    @Size(max = 128, message = "Item name must not exceed 128 characters")
    private String name;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price format: up to 8 digits, 2 decimal places")
    private BigDecimal price;

    private String imageUrl;

    private String description;

    @NotNull(message = "Status is required")
    @Min(value = 0, message = "Status must be 0 or 1")
    @Max(value = 1, message = "Status must be 0 or 1")
    private Integer status;

    private Integer sortOrder;

    private List<CustomizationRequest> customizations;

    //Nested DTO for customization options.
    @Getter
    public static class CustomizationRequest {

        @NotBlank(message = "Option name is required")
        @Size(max = 64)
        private String optionName;

        @NotNull(message = "Choices are required")
        @Size(min = 1, message = "At least one choice is required")
        private List<String> choices;
    }
}