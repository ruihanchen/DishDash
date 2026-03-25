package com.chendev.dishdash.domain.order;

import com.chendev.dishdash.common.response.ApiResponse;
import com.chendev.dishdash.domain.order.dto.OrderRequest;
import com.chendev.dishdash.domain.order.dto.OrderResponse;
import com.chendev.dishdash.domain.order.dto.OrderSummary;
import com.chendev.dishdash.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@Tag(name = "Orders")
public class OrderController {

    private final OrderService orderService;

    // Customer endpoints

    @PostMapping("/api/v1/customer/orders")
    @Operation(summary = "Place a new order from current cart")
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        orderService.placeOrder(principal.getId(), request)));
    }

    @GetMapping("/api/v1/customer/orders")
    @Operation(summary = "List my orders")
    public ResponseEntity<ApiResponse<Page<OrderSummary>>> listMyOrders(
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                ApiResponse.success(orderService.listMyOrders(principal.getId(), pageable)));
    }

    @GetMapping("/api/v1/customer/orders/{id}")
    @Operation(summary = "Get order detail")
    public ResponseEntity<ApiResponse<OrderResponse>> getMyOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                ApiResponse.success(orderService.getMyOrder(id, principal.getId())));
    }

    @PatchMapping("/api/v1/customer/orders/{id}/cancel")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                ApiResponse.success(orderService.cancelOrder(id, principal.getId())));
    }

    // Staff endpoints

    @GetMapping("/api/v1/management/orders")
    @Operation(summary = "Staff: list all orders")
    public ResponseEntity<ApiResponse<Page<OrderSummary>>> listAllOrders(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success(orderService.listAllOrders(pageable)));
    }

    @PatchMapping("/api/v1/management/orders/{id}/status")
    @Operation(summary = "Staff: update order status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        return ResponseEntity.ok(
                ApiResponse.success(orderService.updateStatus(id, status)));
    }
}
