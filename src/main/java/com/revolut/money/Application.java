package com.revolut.money;

import com.revolut.money.rest.controller.AccountsController;
import com.revolut.money.rest.handler.GetBalanceRequestHandler;
import com.revolut.money.rest.handler.PutRequestHandler;
import com.revolut.money.rest.handler.TransferRequestHandler;
import com.revolut.money.service.AccountService;
import com.revolut.money.util.DataSourceProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {
    // @todo injection
    public static void main(String[] args) {
        AccountService accountService = new AccountService(DataSourceProvider.getDataSource());
        TransferRequestHandler transferRequestHandler = new TransferRequestHandler(accountService);
        GetBalanceRequestHandler getBalanceRequestHandler = new GetBalanceRequestHandler(accountService);

        PutRequestHandler putRequestHandler = new PutRequestHandler(accountService);
        AccountsController accountsController = new AccountsController(transferRequestHandler, putRequestHandler, getBalanceRequestHandler);

        accountsController.registerRoutes();
    }
}