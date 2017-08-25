package io.github.volyx.ratpack.model;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.Serializable;
import java.util.Objects;

public class Visit implements Serializable {
    public Integer id;
    public Integer location;
    public Integer user;
    public Long visited_at;
    public Integer mark;

    Visit(Integer id, Integer location, Integer user, Long visited_at, Integer mark) {
        this.id = id;
        this.location = location;
        this.user = user;
        this.visited_at = visited_at;
        this.mark = mark;
    }

    public Visit() {
        this.id = null;
        this.location = null;
        this.user = null;
        this.visited_at = null;
        this.mark = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Visit visit = (Visit) o;
        return Objects.equals(id, visit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    private static final TypeReference<Visit> typeRef = new TypeReference<Visit>() {};
    public static TypeReference<Visit> typeRef() {
        return typeRef;
    }

    @Override
    public String toString() {
        return "Visit{" +
                "id=" + id +
                ", location=" + location +
                ", user=" + user +
                ", visited_at=" + visited_at +
                ", mark=" + mark +
                '}';
    }
}