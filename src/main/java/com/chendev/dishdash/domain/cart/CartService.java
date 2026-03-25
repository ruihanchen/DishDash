package com.chendev.dishdash.domain.cart;

import com.chendev.dishdash.common.exception.BusinessException;
import com.chendev.dishdash.common.exception.ErrorCode;
import com.chendev.dishdash.common.exception.ResourceNotFoundException;
import com.chendev.dishdash.domain.cart.dto.CartItemRequest;
import com.chendev.dishdash.domain.cart.dto.CartResponse;
import com.chendev.dishdash.domain.menu.item.MenuItem;
import com.chendev.dishdash.domain.menu.item.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private static final String CART_KEY_PREFIX = "cart:";
    private static final String ITEM_FIELD_PREFIX = "item:";
    private static final Duration CART_TTL = Duration.ofDays(7);

    private final RedisTemplate<String, Object> redisTemplate;
    private final MenuItemRepository menuItemRepository;

    public CartResponse getCart(Long customerId) {
        String cartKey = cartKey(customerId);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(cartKey);

        List<CartItem> items = entries.values().stream()
                .filter(CartItem.class::isInstance)
                .map(CartItem.class::cast)
                .collect(Collectors.toList());

        return buildCartResponse(items);
    }

    //Adds an item or increments its quantity if already in cart.
    public CartResponse addItem(Long customerId, CartItemRequest request) {
        MenuItem menuItem = menuItemRepository.findActiveById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.MENU_ITEM_NOT_FOUND, "id=" + request.getItemId()));

        if (menuItem.getStatus() != 1) {
            throw new BusinessException(ErrorCode.MENU_ITEM_UNAVAILABLE,
                    "'" + menuItem.getName() + "' is currently unavailable");
        }

        String cartKey = cartKey(customerId);
        String itemField = itemField(request.getItemId());

        // Check if item already in cart — increment quantity if so
        CartItem existing = (CartItem) redisTemplate.opsForHash().get(cartKey, itemField);

        CartItem cartItem = (existing != null)
                ? CartItem.builder()
                .itemId(existing.getItemId())
                .itemName(existing.getItemName())
                .unitPrice(existing.getUnitPrice())
                .imageUrl(existing.getImageUrl())
                .quantity(existing.getQuantity() + request.getQuantity())
                .build()
                : CartItem.builder()
                .itemId(menuItem.getId())
                .itemName(menuItem.getName())
                .unitPrice(menuItem.getPrice())
                .imageUrl(menuItem.getImageUrl())
                .quantity(request.getQuantity())
                .build();

        redisTemplate.opsForHash().put(cartKey, itemField, cartItem);
        redisTemplate.expire(cartKey, CART_TTL);

        log.debug("Cart updated: customerId={}, itemId={}, qty={}",
                customerId, request.getItemId(), cartItem.getQuantity());

        return getCart(customerId);
    }

    //pdates quantity of a specific item.

    public CartResponse updateItem(Long customerId, Long itemId, Integer quantity) {
        String cartKey = cartKey(customerId);
        String itemField = itemField(itemId);

        if (redisTemplate.opsForHash().get(cartKey, itemField) == null) {
            throw new ResourceNotFoundException(
                    ErrorCode.MENU_ITEM_NOT_FOUND, "Item not in cart: id=" + itemId);
        }

        if (quantity <= 0) {
            redisTemplate.opsForHash().delete(cartKey, itemField);
        } else {
            CartItem existing = (CartItem) redisTemplate.opsForHash().get(cartKey, itemField);
            CartItem updated = CartItem.builder()
                    .itemId(existing.getItemId())
                    .itemName(existing.getItemName())
                    .unitPrice(existing.getUnitPrice())
                    .imageUrl(existing.getImageUrl())
                    .quantity(quantity)
                    .build();
            redisTemplate.opsForHash().put(cartKey, itemField, updated);
            redisTemplate.expire(cartKey, CART_TTL);
        }

        return getCart(customerId);
    }

    public CartResponse removeItem(Long customerId, Long itemId) {
        redisTemplate.opsForHash().delete(cartKey(customerId), itemField(itemId));
        return getCart(customerId);
    }

    public void clearCart(Long customerId) {
        redisTemplate.delete(cartKey(customerId));
        log.debug("Cart cleared: customerId={}", customerId);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String cartKey(Long customerId) {
        return CART_KEY_PREFIX + customerId;
    }

    private String itemField(Long itemId) {
        return ITEM_FIELD_PREFIX + itemId;
    }

    private CartResponse buildCartResponse(List<CartItem> items) {
        BigDecimal total = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int itemCount = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return CartResponse.builder()
                .items(items)
                .total(total)
                .itemCount(itemCount)
                .build();
    }
}
