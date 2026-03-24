package com.chendev.dishdash.domain.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;


public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE e.username = :username AND e.deletedAt IS NULL")
    Optional<Employee> findActiveByUsername(String username);

    boolean existsByUsernameAndDeletedAtIsNull(String username);
}
