package com.rentmate.service.delivery.domain.Mapper;

import com.rentmate.service.delivery.domain.dto.event.DeliveryAssignedEventDto;
import com.rentmate.service.delivery.domain.dto.event.NotificationEvent;

import java.util.HashMap;
import java.util.Map;

public class NotificationMapper {

    public static NotificationEvent toNotificationEvent(DeliveryAssignedEventDto dto) {
        NotificationEvent notification = new NotificationEvent();

        // userId = deliveryManId
        notification.setUserId(dto.getDeliveryManId());

        // eventType as string
        notification.setEventType(dto.getEventType().name());

        Map<String, Object> params = new HashMap<>();
        params.put("deliveryType", dto.getDeliveryType().name());
        params.put("deliveryId", dto.getDeliveryId());
        params.put("orderId", dto.getOrderId());
        params.put("from", dto.getFrom());
        params.put("to", dto.getTo());
        params.put("expectedDeliveryTime", dto.getExpectedDeliveryTime().toString());

        notification.setParams(params);

        return notification;
    }
}

