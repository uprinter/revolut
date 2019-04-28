package com.revolut.money.rest.controller;

import com.google.gson.Gson;
import com.revolut.money.rest.handler.GetBalanceRequestHandler;
import com.revolut.money.rest.handler.PutRequestHandler;
import com.revolut.money.rest.handler.TransferRequestHandler;
import com.revolut.money.rest.request.PutRequest;
import com.revolut.money.rest.request.TransferRequest;
import com.revolut.money.rest.response.ResponseStatus;
import com.revolut.money.rest.response.StandardResponse;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import static spark.Spark.get;
import static spark.Spark.post;

@RequiredArgsConstructor
public class AccountsController extends Controller {
    private final TransferRequestHandler transferRequestHandler;
    private final PutRequestHandler putRequestHandler;
    private final GetBalanceRequestHandler getBalanceRequestHandler;

    public void registerRoutes() {
        get("/accounts/:accountId", (request, response) -> {
            response.type("application/json");

            try {
                String stringId = request.params(":accountId");
                int accountId = Integer.valueOf(stringId);
                BigDecimal balance = getBalanceRequestHandler.handle(accountId);

                StandardResponse standardResponse = StandardResponse.builder()
                        .status(ResponseStatus.SUCCESS)
                        .data(new Gson().toJsonTree(balance))
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
    }
}