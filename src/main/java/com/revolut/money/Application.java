package com.revolut.money;

import com.revolut.money.rest.controller.AccountsController;
import com.revolut.money.rest.handler.PutRequestHandler;
import com.revolut.money.rest.handler.TransferRequestHandler;
import com.revolut.money.service.AccountService;
import com.revolut.money.service.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class Application {
    // @todo injection
    public static void main(String[] args) throws SQLException {
        Connection connection = ConnectionFactory.getConnection();

        DSLContext dslContext = DSL.using(connection, SQLDialect.H2);
        AccountService accountService = new AccountService(dslContext);
        TransferRequestHandler transferRequestHandler = new TransferRequestHandler(accountService);

        PutRequestHandler putRequestHandler = new PutRequestHandler(accountService);
        AccountsController accountsController = new AccountsController(transferRequestHandler, putRequestHandler);

        accountsController.registerRoutes();
    }
}