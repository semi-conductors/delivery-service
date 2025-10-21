package com.rentmate.service.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = "com.rentmate.service.delivery.domain.entity")
public class DeliveryServiceApplication {

	public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
	}

}
