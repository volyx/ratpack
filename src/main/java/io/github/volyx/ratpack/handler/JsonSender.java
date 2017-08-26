package io.github.volyx.ratpack.handler;


import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import io.github.volyx.ratpack.model.EmptyObject;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public interface JsonSender {
    EmptyObject EMPTY_OBJECT = new EmptyObject();

    default void sendJson(HttpServerExchange exchange, Object obj) {
        exchange.setStatusCode(200);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(JsonStream.serialize(obj));
    }

    default void sendEmptyJson(HttpServerExchange exchange) {
        exchange.setStatusCode(200);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(JsonStream.serialize(EMPTY_OBJECT));

    }
}