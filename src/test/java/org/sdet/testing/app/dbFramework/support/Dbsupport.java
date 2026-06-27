package org.sdet.testing.app.dbFramework.support;

import org.sdet.testing.app.dbFramework.config.DatabaseConfig;
import org.sdet.testing.app.dbFramework.model.OrderRow;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class Dbsupport {

    private static DatabaseConfig config;

    public Dbsupport(DatabaseConfig config) {
        Dbsupport.config = config;
    }

    public boolean isReachable() throws SQLException {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1");
             ResultSet result = statement.executeQuery()) {
            return result.next() && result.getInt(1) == 1;
        }
    }

    public static OrderRow findOrder(long orderId) throws SQLException {
        String sql = """
        SELECT id, order_number, status, total, user_id, created_at
        FROM orders
        WHERE id = ?
        """;

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                return new OrderRow(
                        result.getLong("id"),
                        result.getString("order_number"),
                        result.getString("status"),
                        result.getBigDecimal("total"),
                        result.getString("user_id"),
                        result.getTimestamp("created_at").toInstant()
                );
            }
        }
    }

    private static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(config.jdbcUrl(), config.username(), config.password());
    }
}
