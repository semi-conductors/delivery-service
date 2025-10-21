package com.rentmate.service.delivery.controller;

import com.rentmate.service.delivery.domain.entity.Delivery;
import com.rentmate.service.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryRepository repository;

    @GetMapping
    public ResponseEntity<List<Delivery>> all() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<List<Delivery>> byRental(@PathVariable Long rentalId) {
        return ResponseEntity.ok(repository.findByRentalId(rentalId));
    }
}

