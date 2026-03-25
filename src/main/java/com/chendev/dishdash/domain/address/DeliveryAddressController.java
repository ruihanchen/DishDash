package com.chendev.dishdash.domain.address;

import com.chendev.dishdash.common.response.ApiResponse;
import com.chendev.dishdash.domain.address.dto.AddressRequest;
import com.chendev.dishdash.domain.address.dto.AddressResponse;
import com.chendev.dishdash.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/addresses")
@RequiredArgsConstructor
@Tag(name = "Delivery Addresses", description = "Customer: manage delivery addresses")
public class DeliveryAddressController {

    private final DeliveryAddressService addressService;

    @GetMapping
    @Operation(summary = "List my delivery addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> listAll(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                ApiResponse.success(addressService.listByCustomer(principal.getId())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a delivery address by ID")
    public ResponseEntity<ApiResponse<AddressResponse>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                ApiResponse.success(addressService.getById(id, principal.getId())));
    }

    @PostMapping
    @Operation(summary = "Add a new delivery address")
    public ResponseEntity<ApiResponse<AddressResponse>> create(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        addressService.create(request, principal.getId())));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a delivery address")
    public ResponseEntity<ApiResponse<AddressResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                ApiResponse.success(addressService.update(id, request, principal.getId())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a delivery address")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        addressService.delete(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PatchMapping("/{id}/default")
    @Operation(summary = "Set an address as default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefault(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                ApiResponse.success(addressService.setDefault(id, principal.getId())));
    }
}
