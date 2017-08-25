package io.github.volyx.ratpack.handler;


import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface JsonParser {
    Logger logger = LoggerFactory.getLogger(JsonParser.class);

    default <T> T parseJson(HttpServerExchange exchange, TypeReference<T> typeRef) {
        try {
            exchange.startBlocking();
            return Json.serializer().fromInputStream(exchange.getInputStream(), typeRef);
        } catch (Json.JsonException e) {
//            logger.error(e.getMessage(), e);
            return null;
        }
    }
}