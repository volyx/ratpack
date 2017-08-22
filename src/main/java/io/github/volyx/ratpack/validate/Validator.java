package io.github.volyx.ratpack.validate;

import javax.annotation.Nonnull;
import java.util.Set;

public interface Validator<T> {
    default String validateJson(@Nonnull String json) {
        if (json.isEmpty()) {
            return "empty json";
        }
        if (json.contains("null")) {
            return "Invalid null value";
        }
        return "";
    }
    @Nonnull
    String validateUpdate(T object);
    @Nonnull
    String validateNew(T object);
}
