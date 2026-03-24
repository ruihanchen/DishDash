package com.chendev.dishdash.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Auth
    INVALID_CREDENTIALS     (HttpStatus.UNAUTHORIZED,  "Invalid username or password."),
    ACCOUNT_SUSPENDED       (HttpStatus.FORBIDDEN,     "Account is suspended. Contact an administrator."),
    TOKEN_EXPIRED           (HttpStatus.UNAUTHORIZED,  "Session expired. Please log in again."),
    TOKEN_INVALID           (HttpStatus.UNAUTHORIZED,  "Invalid authentication token."),
    ACCESS_DENIED           (HttpStatus.FORBIDDEN,     "You do not have permission to perform this action."),

    // Resource not found
    EMPLOYEE_NOT_FOUND      (HttpStatus.NOT_FOUND, "Employee not found."),
    CUSTOMER_NOT_FOUND      (HttpStatus.NOT_FOUND, "Customer not found."),
    MENU_CATEGORY_NOT_FOUND (HttpStatus.NOT_FOUND, "Menu category not found."),
    MENU_ITEM_NOT_FOUND     (HttpStatus.NOT_FOUND, "Menu item not found."),
    COMBO_NOT_FOUND         (HttpStatus.NOT_FOUND, "Combo meal not found."),
    ADDRESS_NOT_FOUND       (HttpStatus.NOT_FOUND, "Delivery address not found."),
    ORDER_NOT_FOUND         (HttpStatus.NOT_FOUND, "Order not found."),

    // Business rule violations
    USERNAME_ALREADY_EXISTS     (HttpStatus.CONFLICT, "Username is already taken."),
    CATEGORY_HAS_ACTIVE_ITEMS   (HttpStatus.CONFLICT, "Cannot delete a category that still has active items."),
    MENU_ITEM_UNAVAILABLE       (HttpStatus.CONFLICT, "This menu item is currently unavailable."),
    CART_EMPTY                  (HttpStatus.CONFLICT, "Cannot place an order with an empty cart."),
    ORDER_NOT_CANCELLABLE       (HttpStatus.CONFLICT, "This order cannot be cancelled at its current stage."),
    INVALID_ORDER_TRANSITION    (HttpStatus.CONFLICT, "Invalid order status transition."),

    // Input validation
    VALIDATION_FAILED       (HttpStatus.BAD_REQUEST, "Request validation failed."),

    // Infrastructure
    FILE_UPLOAD_FAILED      (HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed. Please try again."),
    INTERNAL_ERROR          (HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }
}
