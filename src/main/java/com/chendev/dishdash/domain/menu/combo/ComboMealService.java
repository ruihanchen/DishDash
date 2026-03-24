package com.chendev.dishdash.domain.menu.combo;

import com.chendev.dishdash.common.exception.ErrorCode;
import com.chendev.dishdash.common.exception.ResourceNotFoundException;
import com.chendev.dishdash.domain.menu.category.MenuCategoryRepository;
import com.chendev.dishdash.domain.menu.combo.dto.ComboMealRequest;
import com.chendev.dishdash.domain.menu.combo.dto.ComboMealResponse;
import com.chendev.dishdash.domain.menu.item.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComboMealService {

    private final ComboMealRepository comboMealRepository;
    private final MenuCategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;

    @Transactional(readOnly = true)
    public Page<ComboMealResponse> listAll(Pageable pageable) {
        return comboMealRepository.findAllActive(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ComboMealResponse getById(Long id) {
        return toResponse(findActiveOrThrow(id));
    }

    @Transactional
    public ComboMealResponse create(ComboMealRequest request) {
        validateCategoryExists(request.getCategoryId());

        ComboMeal combo = ComboMeal.builder()
                .name(request.getName())
                .categoryId(request.getCategoryId())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .status(request.getStatus())
                .items(buildComboItems(request))
                .build();

        ComboMeal saved = comboMealRepository.save(combo);
        log.info("Combo meal created: id={}, name={}", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Transactional
    public ComboMealResponse update(Long id, ComboMealRequest request) {
        ComboMeal combo = findActiveOrThrow(id);
        validateCategoryExists(request.getCategoryId());

        combo.setName(request.getName());
        combo.setCategoryId(request.getCategoryId());
        combo.setPrice(request.getPrice());
        combo.setImageUrl(request.getImageUrl());
        combo.setDescription(request.getDescription());
        combo.setStatus(request.getStatus());
        combo.getItems().clear();
        combo.getItems().addAll(buildComboItems(request));

        return toResponse(comboMealRepository.save(combo));
    }

    @Transactional
    public void delete(Long id) {
        ComboMeal combo = findActiveOrThrow(id);
        combo.setDeletedAt(Instant.now());
        comboMealRepository.save(combo);
        log.info("Combo meal soft-deleted: id={}", id);
    }

    private ComboMeal findActiveOrThrow(Long id) {
        return comboMealRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.COMBO_NOT_FOUND, "id=" + id));
    }

    private void validateCategoryExists(Long categoryId) {
        categoryRepository.findActiveById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.MENU_CATEGORY_NOT_FOUND, "id=" + categoryId));
    }

    private List<ComboMealItem> buildComboItems(ComboMealRequest request) {
        return request.getItems().stream()
                .map(i -> ComboMealItem.builder()
                        .itemId(i.getItemId())
                        .quantity(i.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    private ComboMealResponse toResponse(ComboMeal combo) {
        String categoryName = categoryRepository.findActiveById(combo.getCategoryId())
                .map(c -> c.getName())
                .orElse("Unknown");

        List<ComboMealResponse.ComboItemResponse> itemResponses =
                combo.getItems().stream()
                        .map(i -> ComboMealResponse.ComboItemResponse.builder()
                                .itemId(i.getItemId())
                                .itemName(menuItemRepository.findActiveById(i.getItemId())
                                        .map(m -> m.getName())
                                        .orElse("Unknown"))
                                .quantity(i.getQuantity())
                                .build())
                        .collect(Collectors.toList());

        return ComboMealResponse.builder()
                .id(combo.getId())
                .name(combo.getName())
                .categoryId(combo.getCategoryId())
                .categoryName(categoryName)
                .price(combo.getPrice())
                .imageUrl(combo.getImageUrl())
                .description(combo.getDescription())
                .status(combo.getStatus())
                .items(itemResponses)
                .createdAt(combo.getCreatedAt())
                .updatedAt(combo.getUpdatedAt())
                .build();
    }
}
