package io.github.volyx.ratpack.handler;


import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public interface JsonSender {

    default void sendJson(HttpServerExchange exchange, Object obj) {
        exchange.setStatusCode(200);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(ByteBuffer.wrap(Json.serializer().toByteArray(obj)));
    }

    default void sendEmptyJson(HttpServerExchange exchange) {
        exchange.setStatusCode(200);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(ByteBuffer.wrap(Json.serializer().empty().getBytes(StandardCharsets.UTF_8)));

    }
}