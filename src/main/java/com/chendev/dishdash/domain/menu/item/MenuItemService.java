package com.chendev.dishdash.domain.menu.item;

import com.chendev.dishdash.common.exception.BusinessException;
import com.chendev.dishdash.common.exception.ErrorCode;
import com.chendev.dishdash.common.exception.ResourceNotFoundException;
import com.chendev.dishdash.domain.menu.category.MenuCategoryRepository;
import com.chendev.dishdash.domain.menu.item.dto.MenuItemRequest;
import com.chendev.dishdash.domain.menu.item.dto.MenuItemResponse;
import com.chendev.dishdash.domain.menu.item.dto.MenuItemSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuCategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Page<MenuItemSummary> listAll(Pageable pageable) {
        return menuItemRepository.findAllActive(pageable)
                .map(item -> toSummary(item, resolveCategoryName(item.getCategoryId())));
    }

    @Transactional(readOnly = true)
    public Page<MenuItemSummary> listByCategory(Long categoryId, Pageable pageable) {
        return menuItemRepository.findActiveByCategoryId(categoryId, pageable)
                .map(item -> toSummary(item, resolveCategoryName(categoryId)));
    }

    @Transactional(readOnly = true)
    public MenuItemResponse getById(Long id) {
        MenuItem item = findActiveOrThrow(id);
        return toResponse(item, resolveCategoryName(item.getCategoryId()));
    }

    @Transactional
    public MenuItemResponse create(MenuItemRequest request) {
        validateCategoryExists(request.getCategoryId());

        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .categoryId(request.getCategoryId())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .status(request.getStatus())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .customizations(buildCustomizations(request))
                .build();

        MenuItem saved = menuItemRepository.save(item);
        log.info("Menu item created: id={}, name={}", saved.getId(), saved.getName());
        return toResponse(saved, resolveCategoryName(saved.getCategoryId()));
    }

    @Transactional
    public MenuItemResponse update(Long id, MenuItemRequest request) {
        MenuItem item = findActiveOrThrow(id);
        validateCategoryExists(request.getCategoryId());

        item.setName(request.getName());
        item.setCategoryId(request.getCategoryId());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());
        item.setDescription(request.getDescription());
        item.setStatus(request.getStatus());
        item.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);

        // Replace customizations entirely — simpler than diffing the list.
        // orphanRemoval = true on the OneToMany ensures removed entries are deleted.
        item.getCustomizations().clear();
        item.getCustomizations().addAll(buildCustomizations(request));

        return toResponse(menuItemRepository.save(item),
                resolveCategoryName(item.getCategoryId()));
    }

    @Transactional
    public void delete(Long id) {
        MenuItem item = findActiveOrThrow(id);
        item.setDeletedAt(Instant.now());
        menuItemRepository.save(item);
        log.info("Menu item soft-deleted: id={}", id);
    }

    // Helpers

    private MenuItem findActiveOrThrow(Long id) {
        return menuItemRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.MENU_ITEM_NOT_FOUND, "id=" + id));
    }

    private void validateCategoryExists(Long categoryId) {
        categoryRepository.findActiveById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.MENU_CATEGORY_NOT_FOUND, "id=" + categoryId));
    }

    private String resolveCategoryName(Long categoryId) {
        return categoryRepository.findActiveById(categoryId)
                .map(c -> c.getName())
                .orElse("Unknown");
    }

    // Converts request customizations into entities
    private List<ItemCustomization> buildCustomizations(MenuItemRequest request) {
        if (request.getCustomizations() == null) return new ArrayList<>();

        return request.getCustomizations().stream()
                .map(c -> {
                    try {
                        return ItemCustomization.builder()
                                .optionName(c.getOptionName())
                                .choices(objectMapper.writeValueAsString(c.getChoices()))
                                .build();
                    } catch (JsonProcessingException e) {
                        throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                                "Invalid choices format for option: " + c.getOptionName());
                    }
                })
                .collect(Collectors.toList());
    }

    // Deserializes the choices JSON string back to a List<String>.

    private List<String> parseChoices(String choicesJson) {
        try {
            return objectMapper.readValue(choicesJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse choices JSON: {}", choicesJson);
            return new ArrayList<>();
        }
    }

    private MenuItemSummary toSummary(MenuItem item, String categoryName) {
        return MenuItemSummary.builder()
                .id(item.getId())
                .name(item.getName())
                .categoryId(item.getCategoryId())
                .categoryName(categoryName)
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .status(item.getStatus())
                .sortOrder(item.getSortOrder())
                .build();
    }

    private MenuItemResponse toResponse(MenuItem item, String categoryName) {
        List<MenuItemResponse.CustomizationResponse> customizationResponses =
                item.getCustomizations().stream()
                        .map(c -> MenuItemResponse.CustomizationResponse.builder()
                                .id(c.getId())
                                .optionName(c.getOptionName())
                                .choices(parseChoices(c.getChoices()))
                                .build())
                        .collect(Collectors.toList());

        return MenuItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .categoryId(item.getCategoryId())
                .categoryName(categoryName)
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .description(item.getDescription())
                .status(item.getStatus())
                .sortOrder(item.getSortOrder())
                .customizations(customizationResponses)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
