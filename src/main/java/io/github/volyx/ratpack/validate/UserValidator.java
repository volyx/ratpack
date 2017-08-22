package io.github.volyx.ratpack.validate;

import io.github.volyx.ratpack.model.User;

import javax.annotation.Nonnull;

public class UserValidator implements Validator<User> {
    @Nonnull
    @Override
    public String validateUpdate(@Nonnull User object) {
        return "";
    }

    @Nonnull
    @Override
    public String validateNew(User user) {
        if (user == null) {
            return "null";
        }
        if (user.id == null) {
            return "null id";
        }
        if (user.email == null) {
            return "null email";
        }
        if (user.first_name == null) {
            return "null first_name";
        }
        if (user.last_name == null) {
            return "null last_name";
        }
        if (user.gender == null) {
            return "null gender";
        }
        if (user.birth_date == null) {
            return "null birth_date";
        }
        return "";
    }
}
