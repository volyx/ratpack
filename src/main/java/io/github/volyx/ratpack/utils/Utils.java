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

    @Nullable
    public static Integer getInteger(String fromAgeParam, Integer defaultValue) {
        @Nullable final Integer fromAge;
        if (fromAgeParam != null) {
            fromAge = Utils.getIntegerOrDefault(fromAgeParam, null);
        } else {
            fromAge = defaultValue;
        }
        return fromAge;
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
    @Nullable
    public static Long getLong(@Nullable String fromDate, @Nullable Long defaultValue) {
        final Long from;
        if (fromDate != null) {
            from = Utils.getLongOrDefault(fromDate, null);
        } else {
            from = defaultValue;
        }
        return from;
    }

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
//        LocalDate birthDayDate = LocalDateTime.ofEpochSecond(birth_date, 0, ZoneOffset.UTC).toLocalDate();
//        LocalDate currentDate = LocalDateTime.ofEpochSecond(Main.timestamp, 0, ZoneOffset.UTC).toLocalDate();
//        // System.out.println("birthDayDate = " + birthDayDate);
//        // System.out.println("currentDate = " + currentDate);
//        return Period.between(birthDayDate, currentDate).getYears();
//        long timeBetween = Main.timestamp - birth_date;
//        double yearsBetween = timeBetween / 3.156e+10;
//        return  (int) Math.floor(yearsBetween);
        return (int) ((Main.timestamp - birth_date) / 60 / 60 / 24 / 365.25);
    }

    public static void main(String[] args) {
        LocalDate a = LocalDate.of(2015, 2, 2);
        LocalDate b = LocalDate.of(2016, 2, 2);
        // System.out.println(Period.between(a, b).toTotalMonths());
    }

    public static boolean isStringNull(String value) {
        return value.equals("null");
    }
}
