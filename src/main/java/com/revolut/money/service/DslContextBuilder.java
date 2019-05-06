package com.revolut.money.service;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;

class DslContextBuilder {
    DSLContext buildDslContext(Connection connection) {
        return DSL.using(connection, SQLDialect.H2);
    }
}