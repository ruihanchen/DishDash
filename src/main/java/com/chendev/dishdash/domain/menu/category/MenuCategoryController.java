package com.chendev.dishdash.domain.menu.category;

import com.chendev.dishdash.common.response.ApiResponse;
import com.chendev.dishdash.domain.menu.category.dto.MenuCategoryRequest;
import com.chendev.dishdash.domain.menu.category.dto.MenuCategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/v1/management/menu/categories")
@RequiredArgsConstructor
@Tag(name = "Menu Categories", description = "Staff: manage menu categories")
public class MenuCategoryController {

    private final MenuCategoryService categoryService;

    @GetMapping
    @Operation(summary = "List all active categories")
    public ResponseEntity<ApiResponse<List<MenuCategoryResponse>>> listAll(
            @RequestParam(required = false) Integer type) {

        List<MenuCategoryResponse> result = (type != null)
                ? categoryService.listByType(type)
                : categoryService.listAll();

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new menu category")
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> create(
            @Valid @RequestBody MenuCategoryRequest request) {
        MenuCategoryResponse response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a menu category")
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody MenuCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete a menu category")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
