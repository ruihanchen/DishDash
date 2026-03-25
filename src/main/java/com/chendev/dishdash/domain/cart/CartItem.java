package com.chendev.dishdash.domain.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {

    private Long itemId;
    private String itemName;
    private BigDecimal unitPrice;
    private String imageUrl;
    private Integer quantity;

    //Computed field — not stored in Redis separately.
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
