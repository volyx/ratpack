package io.github.volyx.ratpack.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.volyx.ratpack.model.Visit;

import java.io.IOException;

import static io.github.volyx.ratpack.json.UserDeserializer.getValueAsString;

public class VisitDeserializer extends StdDeserializer<Visit> {
    public VisitDeserializer() {
        super((Class<?>) null);
    }

    @Override
    public Visit deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Visit visit = new Visit();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            parser.nextToken();
            switch (fieldName) {
                case "id":
                    String id = getValueAsString(parser);
                    try {
                        visit.id = Integer.parseInt(id);
                    } catch (NumberFormatException e) {
                        visit.id = null;
                    }
                    break;
                case "user":
                    try {
                        visit.user = Integer.parseInt(getValueAsString(parser));
                    } catch (NumberFormatException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "location":
                    try {
                        visit.location = Integer.parseInt(getValueAsString(parser));
                    } catch (NumberFormatException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "visited_at":
                    try {
                        visit.visited_at = Long.parseLong(getValueAsString(parser));
                    } catch (NumberFormatException e) {
//                        throw new RuntimeException(e);
                        visit.visited_at = null;
                    }
                    break;
                case "mark":
                    try {
                        visit.mark = Integer.parseInt(getValueAsString(parser));
                    } catch (NumberFormatException e) {
                        visit.mark = null;
                    }
                    break;
                default:
                    throw new ValidationException("Unsupported field " + fieldName);

            }
        }
        return visit;
    }
}
