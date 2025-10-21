package com.rentmate.service.delivery.domain.entity;

import com.rentmate.service.delivery.domain.enumuration.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import jakarta.persistence.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "deliveries")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rentalId;
    private Long renterId;
    private Long ownerId;

    private String renterAddress ;
    private String ownerAddress;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private BigDecimal deliveryCost;

    private String type ; //forward , return

    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date lastModifiedDate;
}
