package com.rentmate.service.delivery.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {


        public static final String RENTAL_TO_DELIVERY_QUEUE = "rental.delivery.queue";
        public static final String DELIVERY_TO_RENTAL_QUEUE = "delivery.rental.queue";

        @Bean
        public Queue rentalToDeliveryQueue() {
            return new Queue(RENTAL_TO_DELIVERY_QUEUE, true);
        }

        @Bean
        public Queue deliveryToRentalQueue() {
            return new Queue(DELIVERY_TO_RENTAL_QUEUE, true);
        }
    }


