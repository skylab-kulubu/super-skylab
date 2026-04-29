package com.skylab.superapp.core.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class KeycloakDbConfig {

    @Bean(name = "keycloakDataSource")
    @ConfigurationProperties(prefix = "spring.keycloak-datasource")
    public DataSource keycloakDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "keycloakJdbcTemplate")
    public JdbcTemplate keycloakJdbcTemplate(@Qualifier("keycloakDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}