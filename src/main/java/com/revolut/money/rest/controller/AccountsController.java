package com.revolut.money.rest.controller;

import com.revolut.money.rest.handler.PutRequestHandler;
import com.revolut.money.rest.handler.TransferRequestHandler;
import com.revolut.money.rest.request.PutRequest;
import com.revolut.money.rest.request.TransferRequest;
import lombok.RequiredArgsConstructor;

import static spark.Spark.post;

@RequiredArgsConstructor
public class AccountsController extends Controller {
    private final TransferRequestHandler transferRequestHandler;
    private final PutRequestHandler putRequestHandler;

    public void registerRoutes() {
        post("/accounts/transfer", ((request, response) -> {
            response.type("application/json");
            TransferRequest transferRequest = fromRequest(request, TransferRequest.class);

            try {
                transferRequestHandler.handle(transferRequest);
                return buildOkResponse();
            } catch (Exception e) {
                return buildErrorResponse(response, e);
            }
        }));

        post("/accounts/put", ((request, response) -> {
            response.type("application/json");
            PutRequest putRequest = fromRequest(request, PutRequest.class);

            try {
                putRequestHandler.handle(putRequest);
                return buildOkResponse();
            } catch (Exception e) {
                return buildErrorResponse(response, e);
            }
        }));
    }
}