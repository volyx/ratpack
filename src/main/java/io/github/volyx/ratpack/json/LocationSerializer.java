package io.github.volyx.ratpack.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.User;

import java.io.IOException;

public class LocationSerializer extends StdSerializer<Location> {

    public LocationSerializer() {
        this(null);
    }

    public LocationSerializer(Class<Location> t) {
        super(t);
    }

    @Override
    public void serialize(Location location, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeNumberField("id", location.id);
        jgen.writeStringField("country", location.country);
        jgen.writeStringField("place", location.place);
        jgen.writeStringField("city", location.city);
        jgen.writeNumberField("distance", location.distance);
        jgen.writeEndObject();
    }
}
