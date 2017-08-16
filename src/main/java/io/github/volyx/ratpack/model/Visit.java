package io.github.volyx.ratpack.model;

import java.io.Serializable;
import java.util.Objects;

public class Visit implements Serializable {
    public final Integer id;
    public final Integer location;
    public final Integer user;
    public final Long visited_at;
    public final Integer mark;

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