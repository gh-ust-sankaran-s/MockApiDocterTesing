package org.sdet.testing.app.dbFramework.week2_assess.support;

import org.sdet.testing.app.dbFramework.week2_assess.config.week2_db_config;
import org.sdet.testing.app.dbFramework.week2_assess.model.week2_order_row;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class week2_db_support {
    private static week2_db_config config;

    public week2_db_support(week2_db_config config) {
        week2_db_support.config = config;
    }

    public boolean isReachable() throws SQLException {
        try (Connection c = openConnection();
             PreparedStatement stmt = c.prepareStatement("SELECT 1");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) == 1;
        }
    }

    public static week2_order_row findOrder(long id) throws SQLException {
        String sql = "SELECT id, order_number, status, total, user_id, created_at FROM orders WHERE id = ?";
        try (Connection c = openConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new week2_order_row(
                        rs.getLong("id"),
                        rs.getString("order_number"),
                        rs.getString("status"),
                        rs.getBigDecimal("total"),
                        rs.getString("user_id"),
                        rs.getTimestamp("created_at").toInstant()
                );
            }
        }
    }

    private static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(config.jdbcUrl(), config.username(), config.password());
    }
}
