package com.rentmate.service.delivery.domain.dto.event;

import com.rentmate.service.delivery.domain.enumuration.DeliveryEventType;
import com.rentmate.service.delivery.domain.enumuration.DeliveryType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAssignedEventDto {
    private DeliveryEventType eventType; // DELIVERY_ASSIGNED
    private DeliveryType deliveryType; // forward or return
    private Long deliveryId;
    private Long orderId;
    private Long deliveryManId;
    private String from;
    private String to;
    private LocalDateTime expectedDeliveryTime;
}
