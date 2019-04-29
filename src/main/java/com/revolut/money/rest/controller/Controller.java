package com.revolut.money.rest.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.revolut.money.rest.response.ResponseStatus;
import com.revolut.money.rest.response.StandardResponse;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

abstract class Controller {
    <T> T fromRequest(Request request, Class<T> clazz) {
        return new Gson().fromJson(request.body(), clazz);
    }

    Object buildErrorResponse(Response response, Exception e) {
        response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
        StandardResponse errorResponse = StandardResponse.builder().status(ResponseStatus.ERROR).message(e.getMessage()).build();
        return new Gson().toJson(errorResponse);
    }

    Object buildOkResponse() {
        StandardResponse standardResponse = StandardResponse.builder().status(ResponseStatus.SUCCESS).build();
        return new Gson().toJson(standardResponse);
    }

    Object buildOkResponse(JsonElement jsonElement) {
        StandardResponse standardResponse = StandardResponse.builder().status(ResponseStatus.SUCCESS).data(jsonElement).build();
        return new Gson().toJson(standardResponse);
    }
}