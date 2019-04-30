package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.revolut.money.rest.response.ResponseStatus;
import com.revolut.money.rest.response.StandardResponse;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.Optional;

abstract class RequestHandler<T, R> {
    private Class<T> requestClass;

    protected abstract Optional<R> handle(T requestBody, Map<String, String> params);

    RequestHandler(Class<T> requestClass) {
        this.requestClass = requestClass;
    }

    public StandardResponse handleWithJsonResponse(Request request, Response response) {
        try {
            return handleRequest(request, response);
        } catch (Exception e) {
            return buildErrorResponse(response, e);
        }
    }

    private StandardResponse handleRequest(Request request, Response response) {
        T requestObject = parseToRequestObject(request, requestClass);
        Map<String, String> params = request.params();
        Optional<R> returnObject = handle(requestObject, params);

        response.type("application/json");

        if (returnObject.isPresent()) {
            return buildOkResponse(returnObject.get());
        } else {
            return buildOkResponse();
        }
    }

    private T parseToRequestObject(Request request, Class<T> clazz) {
        return new Gson().fromJson(request.body(), clazz);
    }

    private StandardResponse buildErrorResponse(Response response, Exception e) {
        response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
        return StandardResponse.builder().status(ResponseStatus.ERROR).message(e.getMessage()).build();
    }

    private StandardResponse buildOkResponse() {
        return StandardResponse.builder().status(ResponseStatus.SUCCESS).build();
    }

    private StandardResponse buildOkResponse(R returnObject) {
        JsonElement jsonElement = new Gson().toJsonTree(returnObject);
        return StandardResponse.builder().status(ResponseStatus.SUCCESS).data(jsonElement).build();
    }
}