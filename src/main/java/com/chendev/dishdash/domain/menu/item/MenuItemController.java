package com.chendev.dishdash.domain.menu.item;

import com.chendev.dishdash.common.response.ApiResponse;
import com.chendev.dishdash.domain.menu.item.dto.MenuItemRequest;
import com.chendev.dishdash.domain.menu.item.dto.MenuItemResponse;
import com.chendev.dishdash.domain.menu.item.dto.MenuItemSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/management/menu/items")
@RequiredArgsConstructor
@Tag(name = "Menu Items", description = "Staff: manage menu items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping
    @Operation(summary = "List menu items (paginated)")
    public ResponseEntity<ApiResponse<Page<MenuItemSummary>>> listAll(
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<MenuItemSummary> result = (categoryId != null)
                ? menuItemService.listByCategory(categoryId, pageable)
                : menuItemService.listAll(pageable);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get menu item detail")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new menu item")
    public ResponseEntity<ApiResponse<MenuItemResponse>> create(
            @Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(menuItemService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a menu item")
    public ResponseEntity<ApiResponse<MenuItemResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete a menu item")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        menuItemService.delete(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
