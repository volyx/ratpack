package io.github.volyx.ratpack.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.volyx.ratpack.model.Location;

import java.io.IOException;

import static io.github.volyx.ratpack.json.UserDeserializer.getValueAsString;

public class LocationDeserializer extends StdDeserializer<Location> {
    public LocationDeserializer() {
        super((Class<?>) null);
    }

    @Override
    public Location deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Location location = new Location();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            parser.nextToken();
            switch (fieldName) {
                case "id":
                    String id = getValueAsString(parser);
                    try {
                        location.id = Integer.parseInt(id);
                    } catch (NumberFormatException e) {
                        location.id = null;
                    }
                    break;
                case "place":
                    location.place = getValueAsString(parser);
                    break;
                case "city":
                    location.city = getValueAsString(parser);
                    break;
                case "country":
                    location.country = getValueAsString(parser);
                    break;
                case "distance":
                    String distance = getValueAsString(parser);
                    try {
                        location.distance = Integer.parseInt(distance);
                    } catch (NumberFormatException e) {
//                        throw new RuntimeException(e);
                        location.distance = null;
                    }
                    break;
                default:
                    throw new ValidationException("Unsupported field ");

            }
        }
        return location;
    }
}