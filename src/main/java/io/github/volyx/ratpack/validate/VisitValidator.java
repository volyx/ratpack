package io.github.volyx.ratpack.validate;

import io.github.volyx.ratpack.exception.ValidationException;
import io.github.volyx.ratpack.model.Visit;

public class VisitValidator implements Validator<Visit> {
    @Override
    public void validateNew(Visit visit) {
        if (visit == null) {
            throw new ValidationException("visit is null");
        }
        if (visit.id == null) {
            throw new ValidationException("null id");
        }
        if (visit.location == null) {
            throw new ValidationException("null location");
        }
        if (visit.user == null) {
            throw new ValidationException("null user");
        }
        if (visit.visited_at == null) {
            throw new ValidationException("null visited_at");
        }
        if (visit.mark == null) {
            throw new ValidationException("null mark");
        }
        validateMark(visit.mark);
    }
}
