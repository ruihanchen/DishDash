package com.chendev.dishdash.domain.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

    List<DeliveryAddress> findAllByCustomerId(Long customerId);

    Optional<DeliveryAddress> findByIdAndCustomerId(Long id, Long customerId);

    @Modifying
    @Query("UPDATE DeliveryAddress a SET a.isDefault = false WHERE a.customerId = :customerId")
    void clearDefaultByCustomerId(Long customerId);
}