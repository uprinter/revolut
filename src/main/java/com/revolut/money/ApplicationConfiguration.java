package com.revolut.money;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import javax.validation.Validation;
import javax.validation.Validator;

public class ApplicationConfiguration extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:myDb;INIT=runscript from 'classpath:create.sql';DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setAutoCommit(false);
        return new HikariDataSource(config);
    }

    @Provides
    Validator validator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}