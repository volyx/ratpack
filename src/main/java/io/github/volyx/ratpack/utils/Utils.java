package io.github.volyx.ratpack.utils;

import io.github.volyx.ratpack.Main;
import io.github.volyx.ratpack.model.Gender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static io.github.volyx.ratpack.Main.timestamp;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

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


    public static Integer getAge(@Nonnull Long birth_date) {
        LocalDateTime bLDT = LocalDateTime.ofEpochSecond(birth_date, 0, ZoneOffset.UTC);
        LocalDate birthDayDate = bLDT.toLocalDate();
        LocalDateTime cLDT = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
        LocalDate currentDate = cLDT.toLocalDate();
//        System.out.println("Main.timestamp = " + timestamp);
//        System.out.println("birth_date =" + birth_date);
//         System.out.println("birthDayDate = " + birthDayDate);
//         System.out.println("currentDate = " + currentDate);
        int years = Period.between(birthDayDate, currentDate).getYears();
//        logger.info("timestamp {} birth_date {} birthDayDate {} currentDate {} year {}", timestamp, birth_date, DateTimeFormatter.ISO_DATE_TIME.format(bLDT), DateTimeFormatter.ISO_DATE_TIME.format(cLDT), years);
        return years;
//        long timeBetween = Main.timestamp - birth_date;
//        double yearsBetween = timeBetween / 3.156e+10;
//        return  (int) Math.floor(yearsBetween);
//        return (int) ((Main.timestamp - birth_date) / 60 / 60 / 24 / 365.24);

//        LocalDate startDate = LocalDate.of(1987, Month.AUGUST, 10);
//        LocalDate endDate = LocalDate.of(2015, Month.MAY, 27);
//
//        long numberOfYears = ChronoUnit.YEARS.between(startDate, endDate);
//        return 0;
    }

    public static void main(String[] args) {
        LocalDate a = LocalDate.of(2015, 2, 2);
        LocalDate b = LocalDate.of(2016, 2, 2);
        // System.out.println(Period.between(a, b).toTotalMonths());
    }

}
