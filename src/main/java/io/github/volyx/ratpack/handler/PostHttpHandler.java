package io.github.volyx.ratpack.handler;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class PostHttpHandler implements HttpHandler {

    private final HttpHandler delegate;

    public PostHttpHandler(HttpHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (exchange.getRequestMethod().toString().equals("POST")) {
            delegate.handleRequest(exchange);
        } else {
            Exchange.error().notFound(exchange, "Not mapped " + exchange.getRequestURL());
        }
    }
}
