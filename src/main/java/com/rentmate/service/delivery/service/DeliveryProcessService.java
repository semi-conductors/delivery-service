package com.rentmate.service.delivery.service;


import com.rentmate.service.delivery.client.UserClient;
import com.rentmate.service.delivery.domain.dto.event.DeliveryAssignedEventDto;
import com.rentmate.service.delivery.domain.entity.DeliveryGuy;
import com.rentmate.service.delivery.domain.enumuration.*;
import com.rentmate.service.delivery.event.publisher.DeliveryEventPublisher;
import com.rentmate.service.delivery.repository.DeliveryGuyRepository;
import com.rentmate.service.delivery.repository.DeliveryRepository;
import com.rentmate.service.delivery.domain.entity.Delivery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryProcessService {

    private final DeliveryEventPublisher publisher;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryGuyRepository deliveryGuyRepository;
   private final UserClient userClient;


    public void handleDeliveryAction(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        if (delivery.getType().equalsIgnoreCase("FORWARD")) {
           completeForward(deliveryId);
        } else if (delivery.getType().equalsIgnoreCase("RETURN")) {
            completeReturn(deliveryId);
        } else {
            throw new RuntimeException("Unknown delivery type");
        }
    }

    public void startForward(Long rentalId, Long renterId, Long ownerId ,String renterAddress , String ownerAddress , LocalDateTime startDate) {
        DeliveryGuy guy = deliveryGuyRepository.findFirstByStatus(DeliveryManStatus.AVAILABLE)
                .orElseThrow(() -> new RuntimeException("No available delivery guy"));
        //UserResponseDto userResponseDto = userClient.getUserById(renterId) ;
        Delivery delivery = Delivery.builder()
                .rentalId(rentalId)
                .renterId(renterId)
                .ownerId(ownerId)
                .status(DeliveryStatus.SCHEDULED)
                .type("FORWARD")
                .createdDate(new Date())
                .renterAddress(renterAddress)
                .ownerAddress(ownerAddress)
                .scheduledStartTime(startDate)
                .assignedDeliveryGuy(guy)
                .build();

        guy.setStatus(DeliveryManStatus.ASSIGNED);
        guy.setLastUpdated(LocalDateTime.now());
        deliveryGuyRepository.save(guy);

        deliveryRepository.save(delivery);

        publisher.publishAssignedEvent(
                DeliveryAssignedEventDto.builder()
                        .eventType(DeliveryEventType.DELIVERY_ASSIGNED)
                        .deliveryType(DeliveryType.FORWARD)
                        .deliveryId(delivery.getId())
                        .orderId(delivery.getRentalId())
                        .deliveryManId(guy.getId())
                        .from(delivery.getOwnerAddress())
                        .to(delivery.getRenterAddress())
                        .expectedDeliveryTime(delivery.getScheduledStartTime())
                        .build()
        );


        //publisher.publishStatus("delivery.status.updated", rentalId, "OUT_FOR_DELIVERY");
    }
    @Transactional
    @Scheduled(fixedRate = 500) // كل 10 دقايق
    public void checkAndStartDeliveries() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        List<Delivery> dueDeliveries = deliveryRepository
                .findAllByStatusAndScheduledStartTimeBetween(
                        DeliveryStatus.SCHEDULED, now, oneHourLater);

        for (Delivery delivery : dueDeliveries) {
            startDelivery(delivery);
        }
    }

    private void startDelivery(Delivery delivery) {
        delivery.setStatus(DeliveryStatus.IN_PROGRESS);
        delivery.setStartDate(LocalDateTime.now());

        DeliveryGuy guy = delivery.getAssignedDeliveryGuy();
        if (guy != null) {
            guy.setStatus(DeliveryManStatus.ASSIGNED);
            guy.setLastUpdated(LocalDateTime.now());
            deliveryGuyRepository.save(guy);
        }
        deliveryRepository.save(delivery);

    }
    public void completeForward(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setLastModifiedDate(new Date());
        deliveryRepository.save(delivery);

        DeliveryGuy guy = delivery.getAssignedDeliveryGuy();
        if (guy != null) {
            guy.setStatus(DeliveryManStatus.AVAILABLE);
            guy.setLastUpdated(LocalDateTime.now());
            deliveryGuyRepository.save(guy);
        }

        publisher.publishStatus("delivery.delivered", delivery.getRentalId());


    }

    public void startReturn(Long rentalId, Long renterId, Long ownerId , Long itemId , String renterAddress , String ownerAddress ) {
        DeliveryGuy guy = deliveryGuyRepository.findFirstByStatus(DeliveryManStatus.AVAILABLE)
                .orElseThrow(() -> new RuntimeException("No available delivery guy"));

        Delivery delivery = Delivery.builder()
                .rentalId(rentalId)
                .renterId(renterId)
                .ownerId(ownerId)
                .itemId(itemId)
                .status(DeliveryStatus.IN_RETURNING)
                .type("RETURN")
                .createdDate(new Date())
                .ownerAddress(ownerAddress)
                .renterAddress(renterAddress)
                .assignedDeliveryGuy(guy) //assigned
                .build();


        guy.setStatus(DeliveryManStatus.ASSIGNED);
        guy.setLastUpdated(LocalDateTime.now());
        deliveryGuyRepository.save(guy);

        deliveryRepository.save(delivery);



        publisher.publishStatus("delivery.inReturning", rentalId);
        publisher.publishAssignedEvent(
                DeliveryAssignedEventDto.builder()
                        .eventType(DeliveryEventType.DELIVERY_ASSIGNED)
                        .deliveryType(DeliveryType.RETURN)
                        .deliveryId(delivery.getId())
                        .orderId(delivery.getRentalId())
                        .deliveryManId(guy.getId())
                        .from(delivery.getRenterAddress())
                        .to(delivery.getOwnerAddress())
                        .expectedDeliveryTime(LocalDateTime.now().plusHours(2))
                        .build()
        );

    }

    public void completeReturn(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        delivery.setStatus(DeliveryStatus.RETURNED);
        delivery.setLastModifiedDate(new Date());
        deliveryRepository.save(delivery);

        DeliveryGuy guy = delivery.getAssignedDeliveryGuy();
        if (guy != null) {
            guy.setStatus(DeliveryManStatus.AVAILABLE);
            guy.setLastUpdated(LocalDateTime.now());
            deliveryGuyRepository.save(guy);
        }

        publisher.publishStatus("delivery.returned", delivery.getRentalId());


    }
}
