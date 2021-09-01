package com.playtomic.tests.wallet.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class PersistenceJPAConfig {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceJPAConfig.class);
    private static final String PACKAGES_TO_SCAN = "com.playtomic.tests.wallet.domain";

    private final String DB_URL;
    private final String JDBC_DRIVER;
    private final String USER;
    private final String PASSWORD;

    public PersistenceJPAConfig(final H2ConfigProperties properties) {
        this.DB_URL = properties.getUrl();
        this.JDBC_DRIVER = properties.getDriverClassName();
        this.USER = properties.getUsername();
        this.PASSWORD = properties.getPassword();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(JDBC_DRIVER);
        dataSource.setUrl(DB_URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASSWORD);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPackagesToScan(PACKAGES_TO_SCAN);
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
        return entityManagerFactoryBean;
    }
}
