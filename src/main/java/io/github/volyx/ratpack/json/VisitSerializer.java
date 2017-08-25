package io.github.volyx.ratpack.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.volyx.ratpack.model.Visit;

import java.io.IOException;

public class VisitSerializer extends StdSerializer<Visit> {
    public VisitSerializer() {
        super((Class<Visit>) null);

    }
    @Override
    public void serialize(Visit visit, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeNumberField("id", visit.id);
        jgen.writeNumberField("user", visit.user);
        jgen.writeNumberField("mark", visit.mark);
        jgen.writeNumberField("visited_at", visit.visited_at);
        jgen.writeNumberField("location", visit.location);
        jgen.writeEndObject();
    }
}
