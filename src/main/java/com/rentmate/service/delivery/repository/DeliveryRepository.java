package com.rentmate.service.delivery.repository;

import com.rentmate.service.delivery.domain.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByRentalId(Long rentalId);
}
