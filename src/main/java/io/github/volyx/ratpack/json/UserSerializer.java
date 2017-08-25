package io.github.volyx.ratpack.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.volyx.ratpack.model.User;

import java.io.IOException;

public class UserSerializer extends StdSerializer<User> {

    public UserSerializer() {
        this(null);
    }

    public UserSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User user, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeNumberField("id", user.id);
        jgen.writeStringField("email", user.email);
        jgen.writeStringField("last_name", user.last_name);
        jgen.writeStringField("first_name", user.first_name);
        jgen.writeStringField("gender", user.gender.name());
        jgen.writeNumberField("birth_date", user.birth_date);
        jgen.writeEndObject();
    }
}