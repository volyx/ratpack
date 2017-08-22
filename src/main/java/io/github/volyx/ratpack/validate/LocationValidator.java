package io.github.volyx.ratpack.validate;

import io.github.volyx.ratpack.model.Location;

import javax.annotation.Nonnull;

/**
 * id - уникальный внешний id достопримечательности. Устанавливается тестирующей системой. 32-разрядное целое беззнаковоее число.
 * place - описание достопримечательности. Текстовое поле неограниченной длины.
 * country - название страны расположения. unicode-строка длиной до 50 символов.
 * city - название города расположения. unicode-строка длиной до 50 символов.
 * distance - расстояние от города по прямой в километрах. 32-разрядное целое беззнаковое число.
 */
public class LocationValidator implements Validator<Location> {

    @Nonnull
    @Override
    public String validateUpdate(Location location) {
        if (location == null) {
            return "null";
        }
        if (location.id != null) {
            return "id not empty";
        }
        if (location.country != null && isCountryNotValid(location)) {
            return "country gte 50";
        }
        if (location.city != null && isCityNotValid(location)) {
            return "country gte 50";
        }
        return "";
    }

    @Nonnull
    @Override
    public String validateNew(Location location) {
        if (location == null) {
            return "null";
        }
        if (location.id == null) {
            return "null id";
        }
        if (location.country == null) {
            return "null country";
        }
        if (location.city == null) {
            return "null city";
        }
        if (location.place == null) {
            return "null place";
        }
        if (location.distance == null) {
            return "null distance";
        }
        if (isCountryNotValid(location)) {
            return "country gte 50";
        }
        if (isCityNotValid(location)) {
            return "country gte 50";
        }
        return "";
    }

    private boolean isCountryNotValid(Location location) {
        return location.country.length() >= 50;
    }

    private boolean isCityNotValid(Location location) {
        return location.city.length() >= 50;
    }
}
