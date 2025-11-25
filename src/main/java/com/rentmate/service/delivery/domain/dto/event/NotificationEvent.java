package com.rentmate.service.delivery.domain.dto.event;


import java.util.Map;

public class NotificationEvent {
    private String eventType;
    private Long userId;
    private Map<String, Object> params;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}

