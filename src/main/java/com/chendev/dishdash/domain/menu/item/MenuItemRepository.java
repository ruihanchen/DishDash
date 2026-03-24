package com.chendev.dishdash.domain.menu.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;


public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @Query("SELECT i FROM MenuItem i WHERE i.deletedAt IS NULL ORDER BY i.sortOrder ASC")
    Page<MenuItem> findAllActive(Pageable pageable);

    @Query("SELECT i FROM MenuItem i WHERE i.categoryId = :categoryId AND i.deletedAt IS NULL ORDER BY i.sortOrder ASC")
    Page<MenuItem> findActiveByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT i FROM MenuItem i WHERE i.id = :id AND i.deletedAt IS NULL")
    Optional<MenuItem> findActiveById(Long id);

    @Query("SELECT COUNT(i) FROM MenuItem i WHERE i.categoryId = :categoryId AND i.deletedAt IS NULL")
    long countActiveByCategoryId(Long categoryId);

    boolean existsByNameAndDeletedAtIsNull(String name);
}
