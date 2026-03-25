package com.chendev.dishdash.domain.order;

import com.chendev.dishdash.common.exception.BusinessException;
import com.chendev.dishdash.common.exception.ErrorCode;
import com.chendev.dishdash.common.exception.ResourceNotFoundException;
import com.chendev.dishdash.domain.address.DeliveryAddressRepository;
import com.chendev.dishdash.domain.cart.CartItem;
import com.chendev.dishdash.domain.cart.CartService;
import com.chendev.dishdash.domain.cart.dto.CartResponse;
import com.chendev.dishdash.domain.order.dto.OrderRequest;
import com.chendev.dishdash.domain.order.dto.OrderResponse;
import com.chendev.dishdash.domain.order.dto.OrderSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final BigDecimal DELIVERY_FEE = new BigDecimal("2.99");

    // Valid transitions: key = current status, value = allowed next statuses
    private static final java.util.Map<Integer, Set<Integer>> VALID_TRANSITIONS =
            java.util.Map.of(
                    1, Set.of(2, 6),
                    2, Set.of(3, 6),
                    3, Set.of(4),
                    4, Set.of(5),
                    5, Set.of(),
                    6, Set.of()
            );

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final DeliveryAddressRepository addressRepository;

    @Transactional
    public OrderResponse placeOrder(Long customerId, OrderRequest request) {

        // validate cart
        CartResponse cart = cartService.getCart(customerId);
        if (cart.getItems().isEmpty()) {
            throw new BusinessException(ErrorCode.CART_EMPTY);
        }

        // validate address ownership
        addressRepository.findByIdAndCustomerId(request.getAddressId(), customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.ADDRESS_NOT_FOUND, "id=" + request.getAddressId()));

        // snapshot cart → line items
        List<OrderLineItem> lineItems = cart.getItems().stream()
                .map(this::toLineItem)
                .collect(Collectors.toList());

        BigDecimal itemsTotal = cart.getTotal();
        BigDecimal orderTotal = itemsTotal.add(DELIVERY_FEE);

        // persist order
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .customerId(customerId)
                .addressId(request.getAddressId())
                .status(1)
                .amount(orderTotal)
                .deliveryFee(DELIVERY_FEE)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(0)
                .note(request.getNote())
                .lineItems(lineItems)
                .build();

        Order saved = orderRepository.save(order);

        // Step 5: clear cart after successful DB write
        cartService.clearCart(customerId);

        log.info("Order placed: orderId={}, customerId={}, total={}",
                saved.getId(), customerId, orderTotal);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<OrderSummary> listMyOrders(Long customerId, Pageable pageable) {
        return orderRepository
                .findAllByCustomerIdOrderByCreatedAtDesc(customerId, pageable)
                .map(this::toSummary);
    }

    @Transactional(readOnly = true)
    public OrderResponse getMyOrder(Long id, Long customerId) {
        Order order = orderRepository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.ORDER_NOT_FOUND, "id=" + id));
        return toResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long id, Long customerId) {
        Order order = orderRepository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.ORDER_NOT_FOUND, "id=" + id));

        validateTransition(order.getStatus(), 6);
        order.setStatus(6);
        return toResponse(orderRepository.save(order));
    }

    // Staff-side status update
    @Transactional
    public OrderResponse updateStatus(Long id, Integer newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.ORDER_NOT_FOUND, "id=" + id));

        validateTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);

        if (newStatus == 4) {
            // Estimate delivery 45 min from dispatch
            order.setEstimatedDeliveryAt(Instant.now().plusSeconds(45 * 60));
        }

        return toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public Page<OrderSummary> listAllOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toSummary);
    }

    // Helpers

    private void validateTransition(Integer current, Integer next) {
        Set<Integer> allowed = VALID_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(next)) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_TRANSITION,
                    "Cannot transition order from status " + current + " to " + next);
        }
    }

    private OrderLineItem toLineItem(CartItem cartItem) {
        return OrderLineItem.builder()
                .itemName(cartItem.getItemName())
                .imageUrl(cartItem.getImageUrl())
                .unitPrice(cartItem.getUnitPrice())
                .quantity(cartItem.getQuantity())
                .subtotal(cartItem.getSubtotal())
                .build();
    }

    private String generateOrderNumber() {
        return "DD-" + System.currentTimeMillis();
    }

    private OrderSummary toSummary(Order order) {
        return OrderSummary.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .amount(order.getAmount())
                .itemCount(order.getLineItems().stream()
                        .mapToInt(OrderLineItem::getQuantity).sum())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderResponse.LineItemResponse> lineItemResponses = order.getLineItems()
                .stream()
                .map(li -> OrderResponse.LineItemResponse.builder()
                        .id(li.getId())
                        .itemName(li.getItemName())
                        .imageUrl(li.getImageUrl())
                        .unitPrice(li.getUnitPrice())
                        .quantity(li.getQuantity())
                        .subtotal(li.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .amount(order.getAmount())
                .deliveryFee(order.getDeliveryFee())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .note(order.getNote())
                .rejectionReason(order.getRejectionReason())
                .estimatedDeliveryAt(order.getEstimatedDeliveryAt())
                .createdAt(order.getCreatedAt())
                .lineItems(lineItemResponses)
                .build();
    }
}
