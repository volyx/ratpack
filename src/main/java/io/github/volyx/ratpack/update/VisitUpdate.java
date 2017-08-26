package io.github.volyx.ratpack.update;

import com.jsoniter.annotation.JsonObject;

@JsonObject(asExtraForUnknownProperties = true)
public class VisitUpdate {
    public Integer location;
    public Integer user;
    public Long visited_at;
    public Integer mark;
}
