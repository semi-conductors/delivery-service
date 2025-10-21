package com.rentmate.service.delivery.service;


import com.rentmate.service.delivery.event.publisher.DeliveryEventPublisher;
import com.rentmate.service.delivery.repository.DeliveryRepository;
import com.rentmate.service.delivery.domain.entity.Delivery;
import com.rentmate.service.delivery.domain.enumuration.DeliveryStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryProcessService {

    private final DeliveryEventPublisher publisher;
    private final DeliveryRepository repository;

    public void startForward(Long rentalId, Long renterId, Long ownerId ,String renterAddress , String ownerAddress , LocalDateTime startDate) {
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
                .build();
        repository.save(delivery);


        //publisher.publishStatus("delivery.status.updated", rentalId, "OUT_FOR_DELIVERY");
    }
    @Scheduled(fixedRate = 600000) // كل 10 دقايق
    public void checkAndStartDeliveries() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        // هات كل الدليفريز اللي المفروض تبدأ بعد ساعة أو أقل ولسه ما بدأتش
        List<Delivery> dueDeliveries = repository
                .findAllByStatusAndScheduledStartTimeBetween(
                        DeliveryStatus.SCHEDULED, now, oneHourLater);

        for (Delivery delivery : dueDeliveries) {
            startDelivery(delivery);
        }
    }

    private void startDelivery(Delivery delivery) {
        delivery.setStatus(DeliveryStatus.IN_PROGRESS);
        delivery.setStartDate(LocalDateTime.now());
        repository.save(delivery);

        // ممكن كمان تبعتي نوتيفيكيشن أو ميسج لـ RabbitMQ
        // deliveryEventPublisher.publishStartEvent(delivery);
    }
    public void completeForward(Long rentalId) {
        // update last delivery related to rental
        repository.findByRentalId(rentalId).stream().findFirst().ifPresent(d -> {
            d.setStatus(DeliveryStatus.DELIVERED);
            d.setLastModifiedDate(new Date());
            repository.save(d);
        });
        publisher.publishStatus("delivery.delivered", rentalId);
    }

    public void startReturn(Long rentalId, Long renterId, Long ownerId , Long itemId , String renterAddress , String ownerAddress ) {
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
                .build();
        repository.save(delivery);
        publisher.publishStatus("delivery.inReturning", rentalId);
    }

    public void completeReturn(Long rentalId) {
        repository.findByRentalId(rentalId).stream().findFirst().ifPresent(d -> {
            d.setStatus(DeliveryStatus.RETURNED);
            d.setLastModifiedDate(new Date());
            repository.save(d);
        });
        publisher.publishStatus("delivery.returned", rentalId);
    }
}
