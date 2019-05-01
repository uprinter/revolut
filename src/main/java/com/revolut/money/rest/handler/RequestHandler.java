package com.revolut.money.rest.handler;

import com.revolut.money.rest.response.ResponseStatus;
import com.revolut.money.rest.response.StandardResponse;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

public abstract class RequestHandler<T, S> {
    private final RequestValidator<T> requestValidator;

    protected abstract S handle(T requestBody);
    protected abstract T buildRequestObject(Request request);

    RequestHandler(RequestValidator<T> requestValidator) {
        this.requestValidator = requestValidator;
    }

    public StandardResponse<S> handle(Request request, Response response) {
        try {
            return handleRequest(request);
        } catch (Exception e) {
            return buildErrorResponse(response, e);
        }
    }

    private StandardResponse<S> handleRequest(Request request) {
        T requestObject = buildRequestObject(request);

        requestValidator.validate(requestObject);

        S returnObject = handle(requestObject);

        return buildOkResponse(returnObject);
    }

    private StandardResponse<S> buildErrorResponse(Response response, Exception e) {
        response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
        StandardResponse<S> standardResponse = new StandardResponse<>();
        standardResponse.setStatus(ResponseStatus.ERROR);
        standardResponse.setMessage(e.getMessage());
        return standardResponse;
    }

    private StandardResponse<S> buildOkResponse(S returnObject) {
        StandardResponse<S> standardResponse = new StandardResponse<>();
        standardResponse.setStatus(ResponseStatus.SUCCESS);
        standardResponse.setData(returnObject);
        return standardResponse;
    }
}