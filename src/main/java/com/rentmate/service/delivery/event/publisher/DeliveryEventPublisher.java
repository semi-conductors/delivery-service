package com.rentmate.service.delivery.event.publisher;

import com.rentmate.service.delivery.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class DeliveryEventPublisher {

        private final RabbitTemplate rabbitTemplate;

        public void publishDeliveryCost(Long rentalId, BigDecimal cost) {
            Map<String,Object> msg = Map.of(
                    "eventType", "delivery.deliveryCost",
                    "rentalId", rentalId,
                   // "renterId", renterId,
                    "deliveryCost", cost
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.DELIVERY_TO_RENTAL_QUEUE, msg);
        }

        public void publishStatus(String eventType, Long rentalId) {
            Map<String,Object> msg = Map.of(
                    "eventType", eventType,
                    "rentalId", rentalId
                    //"status", status
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.DELIVERY_TO_RENTAL_QUEUE, msg);
        }
    }


