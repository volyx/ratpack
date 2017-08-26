package io.github.volyx.ratpack.update;

import com.jsoniter.annotation.JsonObject;
import io.github.volyx.ratpack.model.Gender;

@JsonObject(asExtraForUnknownProperties = true)
public class UserUpdate {
    public String email;
    public String first_name;
    public String last_name;
    public Gender gender;
    public Long birth_date;
}
