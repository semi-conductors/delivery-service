package com.rentmate.service.delivery.controller;

import com.rentmate.service.delivery.client.UserClient;
import com.rentmate.service.delivery.domain.dto.rest.DeliveryDetailsResponse;
import com.rentmate.service.delivery.domain.dto.rest.UserResponseDto;
import com.rentmate.service.delivery.domain.entity.Delivery;
import com.rentmate.service.delivery.domain.entity.DeliveryGuy;
import com.rentmate.service.delivery.repository.DeliveryRepository;
import com.rentmate.service.delivery.service.DeliveryProcessService;
import com.rentmate.service.delivery.shared.utility.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DeliveryController {

    private final DeliveryRepository repository;
    private final DeliveryProcessService deliveryProcessService;


    private final JwtUtils jwtService;
    private final DeliveryRepository deliveryRepository ;
    private final UserClient userClient;


    @GetMapping("/my")
    public ResponseEntity<List<DeliveryDetailsResponse>> getMyDeliveries(HttpServletRequest request) {
        Long userId = jwtService.getExtractedId(request);
        List<Delivery> deliveries = repository.findByAssignedDeliveryGuy_Id(userId);

        List<DeliveryDetailsResponse> response = deliveries.stream()
                .map(d -> {
                    DeliveryDetailsResponse dto = new DeliveryDetailsResponse();
                    dto.setId(d.getId());
                    dto.setType(d.getType());
                    dto.setStatus(d.getStatus().name());

                    // ✅ أضف الـ fields المطلوبة في الفرونت
                    if ("FORWARD".equalsIgnoreCase(d.getType())) {
                        dto.setPickupName(d.getOwnerName());
                        dto.setPickupAddress(d.getOwnerAddress());
                        dto.setPickupPhone(d.getOwnerPhone());

                        dto.setDropoffName(d.getRenterName());
                        dto.setDropoffAddress(d.getRenterAddress());
                        dto.setDropoffPhone(d.getRenterPhone());
                    } else {
                        dto.setPickupName(d.getRenterName());
                        dto.setPickupAddress(d.getRenterAddress());
                        dto.setPickupPhone(d.getRenterPhone());

                        dto.setDropoffName(d.getOwnerName());
                        dto.setDropoffAddress(d.getOwnerAddress());
                        dto.setDropoffPhone(d.getOwnerPhone());
                    }

                    dto.setDeliveryGuyName(
                            d.getAssignedDeliveryGuy() != null ? d.getAssignedDeliveryGuy().getName() : "Not Assigned"
                    );
                    dto.setDeliveryCost(d.getDeliveryCost());
                    dto.setCreatedDate(d.getCreatedDate());
                    dto.setLastModifiedDate(d.getLastModifiedDate());
                    dto.setRentalId(d.getRentalId());
                    dto.setScheduledStartTime(d.getScheduledStartTime());

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryDetailsResponse> getDeliveryDetails(
            @PathVariable Long deliveryId,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization");

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        UserResponseDto owner = userClient.getUserById(delivery.getOwnerId(), token);
        UserResponseDto renter = userClient.getUserById(delivery.getRenterId(), token);

        DeliveryDetailsResponse response = new DeliveryDetailsResponse();
        response.setId(delivery.getId());
        response.setType(delivery.getType());
        response.setStatus(delivery.getStatus().name());
        response.setDeliveryCost(delivery.getDeliveryCost());
        response.setDeliveryGuyName(delivery.getAssignedDeliveryGuy() != null
                ? delivery.getAssignedDeliveryGuy().getName()
                : "Not Assigned");

        response.setCreatedDate(delivery.getCreatedDate());
        response.setLastModifiedDate(delivery.getLastModifiedDate());

        if ("FORWARD".equalsIgnoreCase(delivery.getType())) {
            // من الـ Owner إلى الـ Renter
            response.setPickupName(owner.getUsername());
            response.setPickupAddress(delivery.getOwnerAddress());
            response.setPickupPhone(owner.getPhoneNumber());

            response.setDropoffName(renter.getUsername());
            response.setDropoffAddress(delivery.getRenterAddress());
            response.setDropoffPhone(renter.getPhoneNumber());
        } else {
            // من الـ Renter إلى الـ Owner
            response.setPickupName(renter.getUsername());
            response.setPickupAddress(delivery.getRenterAddress());
            response.setPickupPhone(renter.getPhoneNumber());

            response.setDropoffName(owner.getUsername());
            response.setDropoffAddress(delivery.getOwnerAddress());
            response.setDropoffPhone(owner.getPhoneNumber());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<List<Delivery>> byRental(@PathVariable Long rentalId) {
        return ResponseEntity.ok(repository.findByRentalId(rentalId));
    }

    @PostMapping("/{deliveryId}/complete")
    public ResponseEntity<Void> completeDelivery(
            @PathVariable Long deliveryId) {
           deliveryProcessService.handleDeliveryAction(deliveryId);
        return ResponseEntity.ok().build();
    }


}

