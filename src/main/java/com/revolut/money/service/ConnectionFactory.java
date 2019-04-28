package com.revolut.money.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    public static Connection getConnection() throws SQLException {
        String connectionString = "jdbc:h2:mem:myDb;INIT=runscript from 'classpath:create.sql';DB_CLOSE_DELAY=-1";
        String user = "sa";
        String password = "";
        Connection connection = DriverManager.getConnection(connectionString/* + ";LOCK_MODE=1"*/, user, password);
        connection.setAutoCommit(false);
        return connection;
    }
}