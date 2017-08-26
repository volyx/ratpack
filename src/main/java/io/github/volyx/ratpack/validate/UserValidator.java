package io.github.volyx.ratpack.validate;

import io.github.volyx.ratpack.exception.ValidationException;
import io.github.volyx.ratpack.model.User;

public class UserValidator implements Validator<User> {

    @Override
    public void validateNew(User user) {
        if (user == null) {
            throw new ValidationException("null");
        }
        if (user.id == null) {
           throw new ValidationException("null id");
        }
        if (user.email == null) {
           throw new ValidationException("null email");
        }
        if (user.first_name == null) {
           throw new ValidationException("null first_name");
        }
        if (user.last_name == null) {
           throw new ValidationException("null last_name");
        }
        if (user.gender == null) {
           throw new ValidationException("null gender");
        }
        if (user.birth_date == null) {
           throw new ValidationException("null birth_date");
        }
    }
}
