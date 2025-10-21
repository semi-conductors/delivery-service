package com.rentmate.service.delivery.domain.dto.event;


import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalCostRequestedEventDto implements Serializable {
    private String eventType; // "rental.cost.requested"
    private Long rentalId;
    private Long renterId;
    private Long ownerId;

    private String renterAddress;
    private String ownerAddress;


}
