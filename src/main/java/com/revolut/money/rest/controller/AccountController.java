package com.revolut.money.rest.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.revolut.money.rest.handler.*;
import com.revolut.money.rest.response.StandardResponse;
import spark.Request;
import spark.Response;

import static spark.Spark.get;
import static spark.Spark.post;

public class AccountController {
    private final TransferRequestHandler transferRequestHandler;
    private final PutRequestHandler putRequestHandler;
    private final WithdrawRequestHandler withdrawRequestHandler;
    private final CreateAccountRequestHandler createAccountRequestHandler;
    private final GetAccountRequestHandler getAccountRequestHandler;

    @Inject
    public AccountController(TransferRequestHandler transferRequestHandler, PutRequestHandler putRequestHandler, WithdrawRequestHandler withdrawRequestHandler, CreateAccountRequestHandler createAccountRequestHandler, GetAccountRequestHandler getAccountRequestHandler) {
        this.transferRequestHandler = transferRequestHandler;
        this.putRequestHandler = putRequestHandler;
        this.withdrawRequestHandler = withdrawRequestHandler;
        this.createAccountRequestHandler = createAccountRequestHandler;
        this.getAccountRequestHandler = getAccountRequestHandler;
    }

    public void registerRoutesAndRun() {
        get("/accounts/:id", (request, response) -> handle(getAccountRequestHandler, request, response));

        post("/accounts/transfer", (request, response) -> handle(transferRequestHandler, request, response));

        post("/accounts/put", (request, response) -> handle(putRequestHandler, request, response));

        post("/accounts/withdraw", (request, response) -> handle(withdrawRequestHandler, request, response));

        post("/accounts", (request, response) -> handle(createAccountRequestHandler, request, response));
    }

    private JsonElement handle(RequestHandler requestHandler, Request request, Response response) {
        StandardResponse standardResponse = requestHandler.handle(request, response);
        return new Gson().toJsonTree(standardResponse);
    }
}