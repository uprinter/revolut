package com.revolut.money.rest.controller;

import com.google.gson.Gson;
import com.revolut.money.rest.handler.TransferRequestHandler;
import com.revolut.money.rest.request.TransferRequest;
import com.revolut.money.rest.response.ResponseStatus;
import com.revolut.money.rest.response.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.http.HttpStatus;

import static spark.Spark.post;

@RequiredArgsConstructor
public class AccountsController {
    private final TransferRequestHandler transferRequestHandler;

    public void registerRoutes() {
        post("/accounts/transfer", ((request, response) -> {
            response.type("application/json");
            TransferRequest transferRequest = new Gson().fromJson(request.body(), TransferRequest.class);

            try {
                transferRequestHandler.handle(transferRequest);
                StandardResponse standardResponse = StandardResponse.builder().status(ResponseStatus.SUCCESS).build();
                return new Gson().toJson(standardResponse);
            } catch (Exception e) {
                response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                StandardResponse errorResponse = StandardResponse.builder().status(ResponseStatus.ERROR).message(e.getMessage()).build();
                return new Gson().toJson(errorResponse);
            }
        }));
    }
}