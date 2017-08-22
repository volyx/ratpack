package io.github.volyx.ratpack.validate;

import io.github.volyx.ratpack.model.Visit;

import javax.annotation.Nonnull;

public class VisitValidator implements Validator<Visit> {
    @Nonnull
    @Override
    public String validateUpdate(Visit visit) {
        if (visit == null) {
            return "visit is null";
        }

        if (visit.mark != null && visit.mark < 0) {
            return "mark ls 0";
        }
        if (visit.mark != null && visit.mark > 5) {
            return "mark gt 5";
        }
        return "";
    }

    @Nonnull
    @Override
    public String validateNew(Visit visit) {
        if (visit == null) {
            return "visit is null";
        }
        if (visit.id == null) {
            return "null id";
        }
        if (visit.location == null) {
            return "null location";
        }
        if (visit.user == null) {
            return "null user";
        }
        if (visit.visited_at == null) {
            return "null visited_at";
        }
        if (visit.mark == null) {
            return "null mark";
        }
        if (visit.mark < 0) {
            return "mark ls 0";
        }
        if (visit.mark > 5) {
            return "mark gt 5";
        }
        return "";
    }
}
