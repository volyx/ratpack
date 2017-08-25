package io.github.volyx.ratpack.handler;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class GetHttpHandler implements HttpHandler {

        private final HttpHandler delegate;

        public GetHttpHandler(HttpHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            if (exchange.getRequestMethod().toString().equals("GET")) {
                delegate.handleRequest(exchange);
            } else {
                Exchange.error().notFound(exchange, "Not mapped " + exchange.getRequestURL());
            }
        }
    }

