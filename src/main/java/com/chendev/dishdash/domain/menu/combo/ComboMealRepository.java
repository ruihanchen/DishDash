package com.chendev.dishdash.domain.menu.combo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ComboMealRepository extends JpaRepository<ComboMeal, Long> {

    @Query("SELECT c FROM ComboMeal c WHERE c.deletedAt IS NULL ORDER BY c.id DESC")
    Page<ComboMeal> findAllActive(Pageable pageable);

    @Query("SELECT c FROM ComboMeal c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<ComboMeal> findActiveById(Long id);
}
