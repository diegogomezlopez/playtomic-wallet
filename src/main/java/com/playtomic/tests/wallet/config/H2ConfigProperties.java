package com.playtomic.tests.wallet.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.datasource")
@Data
@NoArgsConstructor
public final class H2ConfigProperties {

    private String url;
    private String driverClassName;
    private String username;
    private String password;

}
