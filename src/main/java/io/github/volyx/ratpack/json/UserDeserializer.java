package io.github.volyx.ratpack.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.volyx.ratpack.model.Gender;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.utils.Utils;

import java.io.IOException;

public class UserDeserializer extends StdDeserializer<User> {
    public UserDeserializer() {
        super((Class<?>) null);
    }

    @Override
    public User deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        User user = new User();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            parser.nextToken();
            switch (fieldName) {
                case "id":
                    String id = getValueAsString(parser);
                    try {
                        user.id = Integer.parseInt(id);
                    } catch (NumberFormatException e) {
                        user.id = null;
                    }
                    break;
                case "email":
                    user.email = getValueAsString(parser);
                    break;
                case "last_name":
                    user.last_name = getValueAsString(parser);
                    break;
                case "first_name":
                    user.first_name = getValueAsString(parser);
                    break;
                case "gender":
                    String genderString = getValueAsString(parser);
                    if (Utils.isNullOrEmpty(genderString)) {
                        user.gender = null;
                    } else {
                        user.gender = Gender.valueOf(genderString);
                    }
                    break;
                case "birth_date":
                    String birthDay = getValueAsString(parser);
                    user.birth_date = parser.getValueAsLong();
                    try {
                        user.birth_date = Long.parseLong(birthDay);
                    } catch (NumberFormatException e) {
//                        throw new RuntimeException(e);
                        user.birth_date = null;
                    }
                    break;
                default:
                    throw new ValidationException("Unsupported field ");

            }
        }
        return user;
    }

    public static String getValueAsString(JsonParser parser) throws IOException {
        String value = parser.getText();
        if (value == null) {
            return null;
        }
//        System.out.println(value);
        if (Utils.isStringNull(value)) {
            throw new ValidationException("Null ");
        }
        return value;
    }
}
