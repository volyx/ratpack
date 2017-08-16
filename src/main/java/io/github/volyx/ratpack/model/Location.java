package io.github.volyx.ratpack.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

public class Location implements Serializable {
    public final Integer id;
    public final String place;
    @Max(value = 50, message = "country should not be greater than 50")
    public final String country;
    @Max(value = 50, message = "city should not be greater than 50")
    public final String city;
    @Min(value = 5, message = "distance should be gte 0")
    @Max(value = 5, message = "distance should be lte 5")
    public final Integer distance;

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
