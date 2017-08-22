package io.github.volyx.ratpack.utils;

import io.github.volyx.ratpack.Main;
import io.github.volyx.ratpack.model.Gender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneOffset;

public class Utils {

    public static Integer getIntegerOrDefault(@Nullable String value, @Nullable Integer defaultValue) {
        Integer result = defaultValue;
        try {
            if (value != null) {
                result = Integer.parseInt(value);
            }
        } catch (NumberFormatException ignored) {
        }
        return result;
    }

    @Nullable
    public static Long getLongOrDefault(@Nullable String value, @Nullable Long defaultValue) {
        Long result = defaultValue;
        try {
            if (value != null) {
                result = Long.parseLong(value);
            }
        } catch (NumberFormatException ignored) {
        }
        return result;
    }

    public static Gender getGenderOrDefault(@Nullable String gender, @Nullable Gender defaultGender) {
        Gender g = defaultGender;
        try {
            if (gender != null) {
                g = Gender.valueOf(gender);
            }
        } catch (Exception ignored) {
        }
        return g;
    }

    public static Integer getAge(@Nonnull Long birth_date) {
        LocalDate birthDayDate = LocalDateTime.ofEpochSecond(birth_date, 0, ZoneOffset.UTC).toLocalDate();
        LocalDate currentDate = LocalDateTime.ofEpochSecond(Main.timestamp, 0, ZoneOffset.UTC).toLocalDate();
        return Period.between(birthDayDate, currentDate).getYears();
    }
}
