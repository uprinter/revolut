package com.revolut.money;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.money.rest.controller.AccountController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ApplicationConfiguration());
        AccountController accountController = injector.getInstance(AccountController.class);
        accountController.registerRoutesAndRun();
    }
}