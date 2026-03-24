package com.chendev.dishdash.domain.menu.combo;

import com.chendev.dishdash.common.response.ApiResponse;
import com.chendev.dishdash.domain.menu.combo.dto.ComboMealRequest;
import com.chendev.dishdash.domain.menu.combo.dto.ComboMealResponse;
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
@RequestMapping("/api/v1/management/menu/combos")
@RequiredArgsConstructor
@Tag(name = "Combo Meals", description = "Staff: manage combo meals")
public class ComboMealController {

    private final ComboMealService comboMealService;

    @GetMapping
    @Operation(summary = "List combo meals (paginated)")
    public ResponseEntity<ApiResponse<Page<ComboMealResponse>>> listAll(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(comboMealService.listAll(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get combo meal detail")
    public ResponseEntity<ApiResponse<ComboMealResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(comboMealService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a combo meal")
    public ResponseEntity<ApiResponse<ComboMealResponse>> create(
            @Valid @RequestBody ComboMealRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(comboMealService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a combo meal")
    public ResponseEntity<ApiResponse<ComboMealResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ComboMealRequest request) {
        return ResponseEntity.ok(ApiResponse.success(comboMealService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete a combo meal")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        comboMealService.delete(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
