package com.rentmate.service.delivery.domain.dto.event;

import lombok.Data;

import java.io.Serializable;

@Data
public class RentalDeliveryRequestedEventDto implements Serializable {
    private String eventType; // "rental.delivery.requested"
    private Long rentalId;
    private Long renterId;
    private Long ownerId;

    private String renterAddress;
    private String ownerAddress;

}