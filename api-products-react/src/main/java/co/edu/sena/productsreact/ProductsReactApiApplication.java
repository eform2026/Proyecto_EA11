package co.edu.sena.productsreact;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductsReactApiApplication {

    public static void main(String[] args) {
        loadDotEnv();
        configureSpringPropertiesFromEnv();
        String envProfile = property("SPRING_PROFILES_ACTIVE");
        if (envProfile != null && !envProfile.isBlank()
                && System.getProperty("spring.profiles.active") == null) {
            System.setProperty("spring.profiles.active", envProfile);
        }
        SpringApplication.run(ProductsReactApiApplication.class, args);
    }

    private static void loadDotEnv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
            dotenv.entries().forEach(entry -> {
                // Solo seteamos si no existe ya en el entorno real.
                if (System.getProperty(entry.getKey()) == null
                        && System.getenv(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });
        } catch (Exception ex) {
            System.out.println("[dotenv] No se pudo cargar el archivo .env: " + ex.getMessage());
        }
    }

    private static void configureSpringPropertiesFromEnv() {
        setIfMissingFromAny("server.port", "PORT", "SERVER_PORT");

        setIfMissing("app.jwt.secret", "JWT_SECRET");
        setIfMissing("app.jwt.expiration-ms", "JWT_EXPIRATION_MS");
        setIfMissing("app.frontend.url", "FRONTEND_URL");

        setIfMissing("app.mail.enabled", "MAIL_ENABLED");
        setIfMissing("spring.mail.host", "MAIL_HOST");
        setIfMissing("spring.mail.port", "MAIL_PORT");
        setIfMissing("spring.mail.username", "MAIL_USERNAME");
        setIfMissing("spring.mail.password", "MAIL_PASSWORD");

        String profile = firstNonBlank(
                property("spring.profiles.active"),
                property("SPRING_PROFILES_ACTIVE"));
        configureDatasource(profile);
    }

    private static void configureDatasource(String profile) {
        if (profile == null || profile.isBlank()) {
            return;
        }

        String prefix = switch (profile) {
            case "mysql-dev" -> "MYSQL_DEV";
            case "mysql-prod" -> "MYSQL_PROD";
            case "sqlserver-dev" -> "SQLSERVER_DEV";
            case "sqlserver-prod" -> "SQLSERVER_PROD";
            default -> null;
        };

        if (prefix == null) {
            return;
        }

        setIfMissing("spring.datasource.url", prefix + "_URL");
        setIfMissing("spring.datasource.username", prefix + "_USERNAME");
        setIfMissing("spring.datasource.password", prefix + "_PASSWORD");

        if (profile.startsWith("mysql")) {
            setIfMissingValue("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
            setIfMissingValue("spring.jpa.database-platform", "org.hibernate.dialect.MySQLDialect");
        } else if (profile.startsWith("sqlserver")) {
            setIfMissingValue("spring.datasource.driver-class-name", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
            setIfMissingValue("spring.jpa.database-platform", "org.hibernate.dialect.SQLServerDialect");
        }
    }

    private static void setIfMissing(String springProperty, String envProperty) {
        String value = property(envProperty);
        if (value != null && !value.isBlank()) {
            setIfMissingValue(springProperty, value);
        }
    }

    private static void setIfMissingFromAny(String springProperty, String... envProperties) {
        for (String envProperty : envProperties) {
            String value = property(envProperty);
            if (value != null && !value.isBlank()) {
                setIfMissingValue(springProperty, value);
                return;
            }
        }
    }

    private static void setIfMissingValue(String springProperty, String value) {
        if (System.getProperty(springProperty) == null) {
            System.setProperty(springProperty, value);
        }
    }

    private static String property(String key) {
        return firstNonBlank(System.getProperty(key), System.getenv(key));
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }
}
