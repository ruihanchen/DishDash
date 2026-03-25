package com.chendev.dishdash.domain.cart.dto;

import com.chendev.dishdash.domain.cart.CartItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Full cart state returned to the client.
 */
@Getter
@Builder
public class CartResponse {
    private List<CartItem> items;
    private BigDecimal total;
    private Integer itemCount;
}
