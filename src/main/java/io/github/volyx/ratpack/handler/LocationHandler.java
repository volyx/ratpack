package io.github.volyx.ratpack.handler;

import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import io.github.volyx.ratpack.Main;
import io.github.volyx.ratpack.model.Avg;
import io.github.volyx.ratpack.model.Gender;
import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.repository.Repository;
import io.github.volyx.ratpack.update.LocationUpdate;
import io.github.volyx.ratpack.utils.Utils;
import io.github.volyx.ratpack.validate.LocationValidator;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

import static com.codahale.metrics.MetricRegistry.name;

public class LocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(LocationHandler.class);
    private final Repository repository;
    private final LocationValidator validator = new LocationValidator();
    private final Timer getTimer;
    private final Timer updateTimer;
    private final Timer createTimer;
    private final Timer avgTimer;

    public LocationHandler(Repository repository) {
        this.repository = repository;
        this.getTimer = Main.metricRegistry.timer(name(LocationHandler.class, "get"));
        this.updateTimer = Main.metricRegistry.timer(name(LocationHandler.class, "udpate"));
        this.createTimer = Main.metricRegistry.timer(name(LocationHandler.class, "create"));
        this.avgTimer = Main.metricRegistry.timer(name(LocationHandler.class, "avg"));
    }

    public void get(HttpServerExchange exchange) {
        final Timer.Context context = getTimer.time();
        try {
            PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
            String idParam = pathMatch.getParameters().get("id");
            Integer id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                Exchange.error().notFound(exchange, "Bad format " + idParam);
                return;
            }

            @Nullable Location location = repository.findById(id, Location.class);
            if (location == null) {
                Exchange.error().notFound(exchange, "Not found " + idParam);
                return;
            }
            Exchange.body().sendJson(exchange, location);
        } finally {
            context.stop();
        }
    }

    public void update(HttpServerExchange exchange) {
        final Timer.Context context = updateTimer.time();
        try {

            PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
            String idParam = pathMatch.getParameters().get("id");
            Integer id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                Exchange.error().notFound(exchange, "Not found " + idParam);
                return;
            }
            @Nullable Location location = repository.findById(id, Location.class);
            if (location == null) {
                Exchange.error().notFound(exchange, "Not found " + idParam);
                return;
            }

            LocationUpdate update = Exchange.body().parseJson(exchange, LocationUpdate.class);
            if (update == null) {
                Exchange.error().badRequest(exchange, "Update is null " + location.id);
                return;
            }
            location.id = id;
            if (update.place != null) {
                location.place = update.place;
            }
            if (update.country != null) {
                location.country = update.country;
            }
            if (update.city != null) {
                location.city = update.city;
            }
            if (update.distance != null) {
                location.distance = update.distance;
            }
            repository.save(location);
            Exchange.body().sendEmptyJson(exchange);
        } finally {
            context.stop();
        }
    }

    public void create(@Nonnull HttpServerExchange exchange) {
        final Timer.Context context = createTimer.time();
        try {
            Location location = Exchange.body().parseJson(exchange, Location.class);
            validator.validateNew(location);
            repository.save(location);
            Exchange.body().sendEmptyJson(exchange);
        } finally {
            context.stop();
        }
    }

    public void avg(@Nonnull HttpServerExchange exchange) {
        final Timer.Context context = avgTimer.time();
        try {
            PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
            String idParam = pathMatch.getParameters().get("id");
            Integer id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                Exchange.error().notFound(exchange, "Bad id format " + idParam);
                return;
            }

            final Location location = repository.findById(id, Location.class);
            if (location == null) {
                Exchange.error().notFound(exchange, "Not found location " + idParam);
                return;
            }

            Optional<String> fromDateOpt = Exchange.queryParams().queryParam(exchange, "fromDate");
            @Nullable final Long fromDate;
            if (fromDateOpt.isPresent()) {
                try {
                    fromDate = Long.parseLong(fromDateOpt.get());
                } catch (NumberFormatException ignored) {
                    Exchange.error().badRequest(exchange, "Bad from format " + fromDateOpt.get());
                    return;
                }
            } else {
                fromDate = null;
            }

            Optional<String> toDateOpt = Exchange.queryParams().queryParam(exchange, "toDate");
            @Nullable final Long toDate;
            if (toDateOpt.isPresent()) {
                try {
                    toDate = Long.parseLong(toDateOpt.get());
                } catch (NumberFormatException ignored) {
                    Exchange.error().badRequest(exchange, "Bad to format " + toDateOpt.get());
                    return;
                }
            } else {
                toDate = null;
            }
            Optional<String> fromAgeOpt = Exchange.queryParams().queryParam(exchange, "fromAge");
            @Nullable final Integer fromAge;
            if (fromAgeOpt.isPresent()) {
                try {
                    fromAge = Integer.parseInt(fromAgeOpt.get());
                } catch (NumberFormatException ignored) {
                    Exchange.error().badRequest(exchange, "Bad fromAge format " + fromAgeOpt.get());
                    return;
                }
            } else {
                fromAge = null;
            }

            Optional<String> toAgeOpt = Exchange.queryParams().queryParam(exchange, "toAge");
            @Nullable final Integer toAge;
            if (toAgeOpt.isPresent()) {
                try {
                    toAge = Integer.parseInt(toAgeOpt.get());
                } catch (NumberFormatException ignored) {
                    Exchange.error().badRequest(exchange, "Bad toAge format " + toAgeOpt.get());
                    return;
                }
            } else {
                toAge = null;
            }

            final Optional<String> genderOpt = Exchange.queryParams().queryParam(exchange, "gender");
            @Nullable final Gender gender;
            if (genderOpt.isPresent()) {
                try {
                    gender = Gender.valueOf(genderOpt.get());
                } catch (IllegalArgumentException e) {
                    Exchange.error().badRequest(exchange, "Bad gender format " + genderOpt.get());
                    return;
                }
            } else {
                gender = null;
            }
            int sum = 0;
            int count = 0;
            Collection<Visit> visitByLocation = repository.findByLocationId(location.id);
            for (Visit v : visitByLocation) {
                if (fromDate != null && v.visited_at < fromDate) {
                    continue;
                }

                if (toDate != null && v.visited_at > toDate) {
                    continue;
                }

                User user = repository.findById(v.user, User.class);

                if (user == null) {
                    throw new RuntimeException();
                }

                if (gender != null && !gender.equals(user.gender)) {
                    continue;
                }
//                logger.info("{}", user);
                Integer age = Utils.getAge(user.birth_date);

                if (fromAge != null && age <= fromAge) {
                    continue;
                }

                if (toAge != null && age >= toAge) {
                    continue;
                }

                count++;
                sum += v.mark;
            }

            double avg = (count > 0) ? (double) sum / (double) count : 0.0;
            Avg avgContainer = new Avg(avg);
            Exchange.body().sendJson(exchange, avgContainer);
        } finally {
            context.stop();
        }
    }

    public void test(HttpServerExchange exchange) {
        Exchange.body().sendEmptyJson(exchange);
    }

}
