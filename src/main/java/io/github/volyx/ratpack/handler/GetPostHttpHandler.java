package io.github.volyx.ratpack.handler;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class GetPostHttpHandler implements HttpHandler {

    private final HttpHandler getDelegate;
    private final HttpHandler postDelegate;

    public GetPostHttpHandler(HttpHandler getDelegate, HttpHandler postDelegate) {
        this.getDelegate = getDelegate;
        this.postDelegate = postDelegate;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        switch (exchange.getRequestMethod().toString()) {
            case "GET":
                getDelegate.handleRequest(exchange);
                break;
            case "POST":
                postDelegate.handleRequest(exchange);
                break;
            default:
                Exchange.error().notFound(exchange, "Not mapped " + exchange.getRequestURL());
                break;
        }
    }
}
