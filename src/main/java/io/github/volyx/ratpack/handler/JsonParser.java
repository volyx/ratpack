package io.github.volyx.ratpack.handler;


import com.jsoniter.JsonIterator;
import com.jsoniter.spi.JsonException;
import io.undertow.server.HttpServerExchange;
import netscape.javascript.JSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;

public interface JsonParser {
    default <T> T parseJson(HttpServerExchange exchange, Class<T> typeRef) {
        exchange.startBlocking();
        try (JsonIterator iterator = JsonIterator.parse(exchange.getInputStream(), 1024)) {
            return iterator.read(typeRef);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }
}