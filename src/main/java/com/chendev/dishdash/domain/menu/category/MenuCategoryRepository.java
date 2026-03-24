package com.chendev.dishdash.domain.menu.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

    @Query("SELECT c FROM MenuCategory c WHERE c.deletedAt IS NULL ORDER BY c.sortOrder ASC")
    List<MenuCategory> findAllActive();

    @Query("SELECT c FROM MenuCategory c WHERE c.type = :type AND c.deletedAt IS NULL ORDER BY c.sortOrder ASC")
    List<MenuCategory> findAllActiveByType(Integer type);

    @Query("SELECT c FROM MenuCategory c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<MenuCategory> findActiveById(Long id);

    boolean existsByNameAndDeletedAtIsNull(String name);
}
