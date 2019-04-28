package com.revolut.money.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.net.URL;
import java.util.Objects;

public class DataSourceProvider {
    private DataSourceProvider() {}

    private static DataSource dataSource;

    static {
        URL configFile = DataSourceProvider.class.getClassLoader().getResource("datasource.properties");
        String configFilePath = Objects.requireNonNull(configFile).getPath();
        HikariConfig config = new HikariConfig(configFilePath);
        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}