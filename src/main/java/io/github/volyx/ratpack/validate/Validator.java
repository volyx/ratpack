package io.github.volyx.ratpack.validate;

import io.github.volyx.ratpack.exception.ValidationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Validator<T> {
    void validateNew(T object);


    default void validateNull(@Nullable String value) {
        if (value == null) {
            throw new ValidationException("value is null ");
        }
        if (value.equals("null")) {
            throw new ValidationException("value is 'null' ");
        }
    }
    default void validateCountry(@Nonnull String country) {
        if (country.length() > 50) {
            throw new ValidationException("country not valid " + country);
        }
    }

    default void validateCity(@Nonnull String city) {
        if (city.length() > 50) {
            throw new ValidationException("city not valid " + city);
        }
    }
    default void validateEmail(@Nonnull String email) {
        if (email.length() > 100) {
            throw new ValidationException("city not valid " + email);
        }
    }

    default void validateFirstName(@Nonnull String name) {
        if (name.length() > 50) {
            throw new ValidationException("first name not valid " + name);
        }
    }

    default void validateLastName(@Nonnull String name) {
        if (name.length() > 50) {
            throw new ValidationException("last name not valid " + name);
        }
    }

    default void validateMark(int mark) {
        if (mark < 0 || mark > 5) {
            throw new ValidationException("mark not valid " + mark);
        }
    }

    default void validateJson(@Nonnull String json) {
        if (json.isEmpty()) {
            throw new ValidationException("empty json");
        }
        if (json.contains("null")) {
            throw new ValidationException("Invalid null value");
        }
    }
}
