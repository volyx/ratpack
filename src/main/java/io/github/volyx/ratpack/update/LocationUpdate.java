package io.github.volyx.ratpack.update;

import com.jsoniter.annotation.JsonObject;

@JsonObject(asExtraForUnknownProperties = true)
public class LocationUpdate {
    public String place;
    public String country;
    public String city;
    public Integer distance;
}
