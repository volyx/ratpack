package io.github.volyx.ratpack.model;


import com.jsoniter.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

public class Location {
    @JsonProperty(required = true)
    public Integer id;
    @JsonProperty(required = true)
    public String place;
    @JsonProperty(required = true)
    public String country;
    @JsonProperty(required = true)
    public String city;
    @JsonProperty(required = true)
    public Integer distance;

    public Location(Integer id, String place, String country, String city, Integer distance) {
        this.id = id;
        this.place = place;
        this.country = country;
        this.city = city;
        this.distance = distance;
    }

    public Location() {
        this.id = null;
        this.place = null;
        this.country = null;
        this.city = null;
        this.distance = null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(id, location.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", place='" + place + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", distance=" + distance +
                '}';
    }
}
