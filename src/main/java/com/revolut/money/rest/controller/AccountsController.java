package com.revolut.money.rest.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.revolut.money.rest.handler.CreateAccountRequestHandler;
import com.revolut.money.rest.handler.GetAccountRequestHandler;
import com.revolut.money.rest.handler.PutRequestHandler;
import com.revolut.money.rest.handler.TransferRequestHandler;
import com.revolut.money.rest.request.PutRequest;
import com.revolut.money.rest.request.TransferRequest;
import com.revolut.money.rest.response.ResponseStatus;
import com.revolut.money.rest.response.StandardResponse;

import static spark.Spark.get;
import static spark.Spark.post;

public class AccountsController extends Controller {
    private final TransferRequestHandler transferRequestHandler;
    private final PutRequestHandler putRequestHandler;
    private final CreateAccountRequestHandler createAccountRequestHandler;
    private final GetAccountRequestHandler getAccountRequestHandler;

    @Inject
    public AccountsController(TransferRequestHandler transferRequestHandler, PutRequestHandler putRequestHandler, CreateAccountRequestHandler createAccountRequestHandler, GetAccountRequestHandler getAccountRequestHandler) {
        this.transferRequestHandler = transferRequestHandler;
        this.putRequestHandler = putRequestHandler;
        this.createAccountRequestHandler = createAccountRequestHandler;
        this.getAccountRequestHandler = getAccountRequestHandler;
    }

    public void registerRoutesAndRun() {
        get("/accounts/:accountId", (request, response) -> {
            response.type("application/json");

            try {
                String stringId = request.params(":accountId");
                int accountId = Integer.valueOf(stringId);
                JsonElement jsonElement = getAccountRequestHandler.handle(accountId);

                StandardResponse standardResponse = StandardResponse.builder()
                        .status(ResponseStatus.SUCCESS)
                        .data(new Gson().toJsonTree(jsonElement))
                        .build();

                return new Gson().toJson(standardResponse);
            } catch (Exception e) {
                return buildErrorResponse(response, e);
            }
        });

        post("/accounts/transfer", (request, response) -> {
            response.type("application/json");

            try {
                TransferRequest transferRequest = fromRequest(request, TransferRequest.class);
                transferRequestHandler.handle(transferRequest);
                return buildOkResponse();
            } catch (Exception e) {
                return buildErrorResponse(response, e);
            }
        });

        post("/accounts/put", (request, response) -> {
            response.type("application/json");

            try {
                PutRequest putRequest = fromRequest(request, PutRequest.class);
                putRequestHandler.handle(putRequest);
                return buildOkResponse();
            } catch (Exception e) {
                return buildErrorResponse(response, e);
            }
        });

        post("/accounts", (request, response) -> {
            response.type("application/json");

            try {
                JsonElement jsonElement = createAccountRequestHandler.handle();
                return buildOkResponse(jsonElement);
            } catch (Exception e) {
                return buildErrorResponse(response, e);
            }
        });
    }
}