package io.github.volyx.ratpack.handler;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public interface ErrorSender {

    default void notFound(HttpServerExchange exchange, Object obj) {
        exchange.setStatusCode(404);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(ByteBuffer.wrap(Json.serializer().toByteArray(obj)));
    }

    default void notFound(HttpServerExchange exchange, String obj) {
        exchange.setStatusCode(404);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "plain/text");
        exchange.getResponseSender().send(ByteBuffer.wrap(obj.getBytes(Charset.defaultCharset())));
    }
    default void badRequest(HttpServerExchange exchange, String obj) {
        exchange.setStatusCode(400);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "plain/text");
        exchange.getResponseSender().send(ByteBuffer.wrap(obj.getBytes(Charset.defaultCharset())));
    }
}
