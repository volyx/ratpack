package io.github.volyx.ratpack.json;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import io.github.volyx.ratpack.exception.ValidationException;
import io.github.volyx.ratpack.model.Gender;
import io.github.volyx.ratpack.model.User;

import javax.annotation.Nonnull;
import java.io.IOException;

public class UserCodec {

    public User calc(@Nonnull JsonIterator iter) {
        User user = new User();
        try {
            for (String field = iter.readObject(); field != null; field = iter.readObject()) {
                switch (field) {
                    case "id":
                        user.id = iter.readInt();
                        break;
                    case "email":
                        user.email = iter.readString();
                        break;
                    case "first_name":
                        user.first_name = iter.readString();
                        break;
                    case "last_name":
                        user.first_name = iter.readString();
                        break;
                    case "gender":
                        user.gender = Gender.valueOf(iter.readString());
                        break;
                    case "birth_date":
                        user.birth_date = iter.readLong();
                        break;
                    default:
                        iter.skip();
                }
            }
        } catch (IOException io) {
            throw new ValidationException(io);
        }
        return user;
    }

    public static void main(String[] args) {
        try (JsonIterator iter = JsonIterator.parse("{\"id\": 1}")) {
           User user = new UserCodec().calc(iter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
