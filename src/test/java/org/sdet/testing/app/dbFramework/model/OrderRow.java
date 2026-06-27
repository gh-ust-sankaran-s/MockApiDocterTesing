package org.sdet.testing.app.dbFramework.model;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderRow {
    private final long id;
    private final String orderNumber;
    private final String status;
    private final BigDecimal total;
    private final String userId;
    private final Instant createdAt;

    public OrderRow(long id, String orderNumber, String status, BigDecimal total, String userId, Instant createdAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.total = total;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
