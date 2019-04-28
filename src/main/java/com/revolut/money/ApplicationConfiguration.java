package com.revolut.money;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.net.URL;
import java.util.Objects;

public class ApplicationConfiguration extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    DataSource dataSource() {
        URL configFile = ApplicationConfiguration.class.getClassLoader().getResource("datasource.properties");
        String configFilePath = Objects.requireNonNull(configFile).getPath();
        HikariConfig config = new HikariConfig(configFilePath);
        return new HikariDataSource(config);
    }
}