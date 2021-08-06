package com.playtomic.tests.wallet.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class H2Config {

    private static final Logger logger = LoggerFactory.getLogger(H2Config.class);

    private final String DB_URL;
    private final String JDBC_DRIVER;
    private final String USER;
    private final String PASSWORD;

    public H2Config(final H2ConfigProperties properties) {
        this.DB_URL = properties.getUrl();
        this.JDBC_DRIVER = properties.getDriverClassName();
        this.USER = properties.getUsername();
        this.PASSWORD = properties.getPassword();
    }

    @Bean
    public Connection connection() {
        try {
            logger.info("Registering JDBC Driver: {}", JDBC_DRIVER);
            Class.forName(JDBC_DRIVER);
            logger.info("Registering JDBC Driver: {}", JDBC_DRIVER);
            return DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (ClassNotFoundException exception) {
            logger.error("Error registering JDBC Driver: {}, {}", JDBC_DRIVER, exception.getLocalizedMessage());
        } catch (SQLException exception) {
            logger.error("Error connecting to database: {}, {}", DB_URL, exception.getLocalizedMessage());
        }
        return null;
    }


}
