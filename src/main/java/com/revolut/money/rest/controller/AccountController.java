package com.revolut.money.rest.controller;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.revolut.money.rest.handler.CreateAccountRequestHandler;
import com.revolut.money.rest.handler.GetAccountRequestHandler;
import com.revolut.money.rest.handler.PutRequestHandler;
import com.revolut.money.rest.handler.TransferRequestHandler;
import com.revolut.money.rest.response.StandardResponse;

import static spark.Spark.get;
import static spark.Spark.post;

public class AccountController {
    private final TransferRequestHandler transferRequestHandler;
    private final PutRequestHandler putRequestHandler;
    private final CreateAccountRequestHandler createAccountRequestHandler;
    private final GetAccountRequestHandler getAccountRequestHandler;

    @Inject
    public AccountController(TransferRequestHandler transferRequestHandler, PutRequestHandler putRequestHandler, CreateAccountRequestHandler createAccountRequestHandler, GetAccountRequestHandler getAccountRequestHandler) {
        this.transferRequestHandler = transferRequestHandler;
        this.putRequestHandler = putRequestHandler;
        this.createAccountRequestHandler = createAccountRequestHandler;
        this.getAccountRequestHandler = getAccountRequestHandler;
    }

    public void registerRoutesAndRun() {
        get("/accounts/:id", (request, response) -> {
            StandardResponse standardResponse = getAccountRequestHandler.handleWithJsonResponse(request, response);
            return new Gson().toJsonTree(standardResponse);
        });

        post("/accounts/transfer", (request, response) -> {
            StandardResponse standardResponse = transferRequestHandler.handleWithJsonResponse(request, response);
            return new Gson().toJsonTree(standardResponse);
        });

        post("/accounts/put", (request, response) -> {
            StandardResponse standardResponse = putRequestHandler.handleWithJsonResponse(request, response);
            return new Gson().toJsonTree(standardResponse);
        });

        post("/accounts", (request, response) -> {
            StandardResponse standardResponse = createAccountRequestHandler.handleWithJsonResponse(request, response);
            return new Gson().toJsonTree(standardResponse);
        });
    }
}