package com.besquiros.piercingbysamar.entity.enums;

public enum OrderStatus {
    // Shipping flow
    PENDING,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    // Click & Collect flow
    CLICK_COLLECT_PENDING,
    READY,
    COLLECTED
}
