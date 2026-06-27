package org.sdet.testing.app.dbFramework.week2_assess.model;

import java.math.BigDecimal;
import java.time.Instant;

public class week2_order_row {
    private final long id;
    private final String order_number;
    private final String status;
    private final BigDecimal total;
    private final String user_id;
    private final Instant created_at;

    public week2_order_row(long id, String order_number, String status, BigDecimal total, String user_id, Instant created_at) {
        this.id = id;
        this.order_number = order_number;
        this.status = status;
        this.total = total;
        this.user_id = user_id;
        this.created_at = created_at;
    }

    public long getId() {
        return id;
    }

    public String getOrder_number() {
        return order_number;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getUser_id() {
        return user_id;
    }

    public Instant getCreated_at() {
        return created_at;
    }
}
