package io.github.volyx.ratpack.handler;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public interface ErrorSender {

    default void notFound(HttpServerExchange exchange, String obj) {
        exchange.setStatusCode(404);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "plain/text");
        exchange.getResponseSender().send(obj);
    }
    default void badRequest(HttpServerExchange exchange, String obj) {
        exchange.setStatusCode(400);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "plain/text");
        exchange.getResponseSender().send(obj);
    }
}
