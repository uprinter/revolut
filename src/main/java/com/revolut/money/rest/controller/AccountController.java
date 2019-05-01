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
    private final TransferMoneyRequestHandler transferMoneyRequestHandler;
    private final PutMoneyRequestHandler putMoneyRequestHandler;
    private final WithdrawMoneyRequestHandler withdrawMoneyRequestHandler;
    private final CreateAccountRequestHandler createAccountRequestHandler;
    private final GetAccountRequestHandler getAccountRequestHandler;

    @Inject
    public AccountController(TransferMoneyRequestHandler transferMoneyRequestHandler, PutMoneyRequestHandler putMoneyRequestHandler, WithdrawMoneyRequestHandler withdrawMoneyRequestHandler, CreateAccountRequestHandler createAccountRequestHandler, GetAccountRequestHandler getAccountRequestHandler) {
        this.transferMoneyRequestHandler = transferMoneyRequestHandler;
        this.putMoneyRequestHandler = putMoneyRequestHandler;
        this.withdrawMoneyRequestHandler = withdrawMoneyRequestHandler;
        this.createAccountRequestHandler = createAccountRequestHandler;
        this.getAccountRequestHandler = getAccountRequestHandler;
    }

    public void registerRoutesAndRun() {
        get("/accounts/:id", (request, response) -> handle(getAccountRequestHandler, request, response));

        post("/accounts/transfer", (request, response) -> handle(transferMoneyRequestHandler, request, response));

        post("/accounts/put", (request, response) -> handle(putMoneyRequestHandler, request, response));

        post("/accounts/withdraw", (request, response) -> handle(withdrawMoneyRequestHandler, request, response));

        post("/accounts", (request, response) -> handle(createAccountRequestHandler, request, response));
    }

    private JsonElement handle(RequestHandler requestHandler, Request request, Response response) {
        StandardResponse standardResponse = requestHandler.handle(request, response);
        return new Gson().toJsonTree(standardResponse);
    }
}