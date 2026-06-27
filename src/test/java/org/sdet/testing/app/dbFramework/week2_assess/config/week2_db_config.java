package org.sdet.testing.app.dbFramework.week2_assess.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.sdet.testing.app.specFactory_Assignment_2;

public record week2_db_config(String jdbcUrl, String username, String password) {
    public static week2_db_config fromEnv() {
        String jdbcUrl = value("DB_JDBC_URL");
        if (jdbcUrl != null) {
            return new week2_db_config(jdbcUrl, required("DB_USER"), required("DB_PASSWORD"));
        }
        String databaseUrl = required("DATABASE_URL");
        return fromDatabaseUrl(databaseUrl);
    }

    private static week2_db_config fromDatabaseUrl(String databaseUrl) {
        URI uri = URI.create(databaseUrl);
        String[] creds = uri.getRawUserInfo().split(":", 2);
        int port = uri.getPort() == -1 ? 3306 : uri.getPort();
        String query = uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery();
        String url = "jdbc:mysql://" + uri.getHost() + ":" + port + uri.getPath() + query;
        return new week2_db_config(url, decode(creds[0]), decode(creds[1]));
    }

    private static String required(String name) {
        String value = value(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing env or property: " + name);
        }
        return value;
    }

    private static String value(String name) {
        String prop = System.getProperty(name);
        if (prop != null && !prop.isBlank()) return prop;
        String env = System.getenv(name);
        if (env != null && !env.isBlank()) return env;
        // fallback to application.properties loader used elsewhere in tests
        try {
            return specFactory_Assignment_2.config(name, null);
        } catch (Exception e) {
            return null;
        }
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
