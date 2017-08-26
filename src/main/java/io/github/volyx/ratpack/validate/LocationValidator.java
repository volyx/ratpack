package io.github.volyx.ratpack.validate;

import io.github.volyx.ratpack.exception.ValidationException;
import io.github.volyx.ratpack.model.Location;

public class LocationValidator implements Validator<Location> {

    @Override
    public void validateNew(Location location) {
        if (location == null) {
            throw new ValidationException("null");
        }
        if (location.id == null) {
            throw new ValidationException("null id");
        }
        if (location.country == null) {
            throw new ValidationException("null country");
        }
        if (location.city == null) {
            throw new ValidationException("null city");
        }
        if (location.place == null) {
            throw new ValidationException("null place");
        }
        if (location.distance == null) {
            throw new ValidationException("null distance");
        }
        validateCountry(location.country);
        validateCity(location.city);
    }

}
