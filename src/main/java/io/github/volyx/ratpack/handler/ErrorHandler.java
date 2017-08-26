package io.github.volyx.ratpack.handler;

import com.jsoniter.spi.JsonException;
import io.github.volyx.ratpack.exception.ValidationException;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class ErrorHandler implements HttpHandler {

    private HttpHandler handler;

    public ErrorHandler(HttpHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        try {
            if (exchange.isInIoThread()) {
                exchange.dispatch(this);
                return;
            }
            handler.handleRequest(exchange);
        } catch (ValidationException | JsonException e) {
            if(exchange.isResponseChannelAvailable()) {
                Exchange.error().badRequest(exchange, "Validation error " + e.getMessage() );
            }
        }
    }
}