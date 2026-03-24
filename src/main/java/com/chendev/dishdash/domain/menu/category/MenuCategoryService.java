package com.chendev.dishdash.domain.menu.category;

import com.chendev.dishdash.common.exception.BusinessException;
import com.chendev.dishdash.common.exception.ErrorCode;
import com.chendev.dishdash.common.exception.ResourceNotFoundException;
import com.chendev.dishdash.domain.menu.category.dto.MenuCategoryRequest;
import com.chendev.dishdash.domain.menu.category.dto.MenuCategoryResponse;
import com.chendev.dishdash.domain.menu.item.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class MenuCategoryService {

    private final MenuCategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;

    @Transactional(readOnly = true)
    public List<MenuCategoryResponse> listAll() {
        return categoryRepository.findAllActive()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MenuCategoryResponse> listByType(Integer type) {
        return categoryRepository.findAllActiveByType(type)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MenuCategoryResponse getById(Long id) {
        return toResponse(findActiveOrThrow(id));
    }

    @Transactional
    public MenuCategoryResponse create(MenuCategoryRequest request) {
        // Enforce unique name within active categories.
        if (categoryRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                    "A category named '" + request.getName() + "' already exists");
        }

        MenuCategory category = MenuCategory.builder()
                .name(request.getName())
                .type(request.getType())
                .sortOrder(request.getSortOrder())
                .build();

        MenuCategory saved = categoryRepository.save(category);
        log.info("Menu category created: id={}, name={}", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Transactional
    public MenuCategoryResponse update(Long id, MenuCategoryRequest request) {
        MenuCategory category = findActiveOrThrow(id);
        category.setName(request.getName());
        category.setType(request.getType());
        category.setSortOrder(request.getSortOrder());
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        MenuCategory category = findActiveOrThrow(id);

        // Guard: refuse deletion if active items still reference this category.
        // Cascading the delete would silently remove items customers may have
        // in their carts or recent orders — too destructive.
        long activeItemCount = menuItemRepository.countActiveByCategoryId(id);
        if (activeItemCount > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_HAS_ACTIVE_ITEMS,
                    "Category '" + category.getName() + "' still has "
                            + activeItemCount + " active item(s). "
                            + "Disable or reassign them before deleting the category.");
        }

        category.setDeletedAt(Instant.now());
        categoryRepository.save(category);
        log.info("Menu category soft-deleted: id={}", id);
    }

    private MenuCategory findActiveOrThrow(Long id) {
        return categoryRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.MENU_CATEGORY_NOT_FOUND, "id=" + id));
    }

    private MenuCategoryResponse toResponse(MenuCategory category) {
        return MenuCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .sortOrder(category.getSortOrder())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
