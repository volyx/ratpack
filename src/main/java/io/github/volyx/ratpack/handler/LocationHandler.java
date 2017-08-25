package io.github.volyx.ratpack.handler;

import io.github.volyx.ratpack.model.Gender;
import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.repository.LocationRepository;
import io.github.volyx.ratpack.repository.UserRepository;
import io.github.volyx.ratpack.repository.VisitRepository;
import io.github.volyx.ratpack.utils.Utils;
import io.github.volyx.ratpack.validate.LocationValidator;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.volyx.ratpack.utils.Utils.getInteger;
import static io.github.volyx.ratpack.utils.Utils.getLong;

public class LocationHandler {

    private final LocationRepository locationRepository;
    private final VisitRepository visitRepository;
    private final UserRepository userRepository;
    private final LocationValidator validator = new LocationValidator();

    public LocationHandler(@Nonnull LocationRepository locationRepository, @Nonnull VisitRepository visitRepository, @Nonnull UserRepository userRepository) {
        this.locationRepository = locationRepository;
        this.visitRepository = visitRepository;
        this.userRepository = userRepository;
    }

    public void get(HttpServerExchange exchange) {
        PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        String idParam = pathMatch.getParameters().get("id");
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            Exchange.error().notFound(exchange,  "Bad format " + idParam);
            return;
        }

        @Nullable Location location = locationRepository.findById(id);
        if (location == null) {
            Exchange.error().notFound(exchange,  "Not found " + idParam);
            return;
        }
        Exchange.body().sendJson(exchange, location);
    }

    public void update(HttpServerExchange exchange) {
        PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        String idParam = pathMatch.getParameters().get("id");
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            Exchange.error().notFound(exchange,  "Not found " + idParam);
            return;
        }
        @Nullable Location location = locationRepository.findById(id);
        if (location == null) {
            Exchange.error().notFound(exchange,  "Not found " + idParam);
            return;
        }

        Location update = Exchange.body().parseJson(exchange, Location.typeRef());
        if (update == null) {
            Exchange.error().badRequest(exchange, "Update is null " + location.id);
            return;
        }
        String violations = validator.validateUpdate(update);
        if (!violations.isEmpty()) {
            Exchange.error().badRequest(exchange,  "Validation " + violations);
            return;
        }
        location.id = id;
        if (!Utils.isNullOrEmpty(update.place )) {
            location.place = update.place;
        }
        if (!Utils.isNullOrEmpty(update.country )) {
            location.country = update.country;
        }
        if (!Utils.isNullOrEmpty(update.city )) {
            location.city = update.city;
        }
        if (update.distance != null) {
            location.distance = update.distance;
        }
        locationRepository.save(location);
        Exchange.body().sendEmptyJson(exchange);
    }

    public void create(@Nonnull HttpServerExchange exchange) {
        Location location = Exchange.body().parseJson(exchange, Location.typeRef());
        String violations = validator.validateNew(location);
        if (!violations.isEmpty()) {
            Exchange.error().badRequest(exchange,  "Validation " + violations);
            return;
        }
        locationRepository.save(location);
        Exchange.body().sendEmptyJson(exchange);
    }

    public void avg(@Nonnull HttpServerExchange exchange) {
        PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        String idParam = pathMatch.getParameters().get("id");
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            Exchange.error().notFound(exchange,  "Bad id format " + idParam);
            return;
        }

        final Location location = locationRepository.findById(id);
        if (location == null) {
            Exchange.error().notFound(exchange,  "Not found location " + idParam);
            return;
        }

        Optional<String> fromDateOpt = Exchange.queryParams().queryParam(exchange,"fromDate");
        final Long from;
        if (fromDateOpt.isPresent()) {
            from = getLong(fromDateOpt.get(), 0L);
            if (from == null) {
                Exchange.error().badRequest(exchange,  "Bad from format " + fromDateOpt.get());
                return;
            }
        } else {
            from = 0L;
        }

        Optional<String> toDateOpt = Exchange.queryParams().queryParam(exchange,"toDate");
        final Long to;
        if (toDateOpt.isPresent()) {
            to = getLong(toDateOpt.get(), Long.MAX_VALUE);
            if (to == null) {
                Exchange.error().badRequest(exchange,  "Bad to format " + toDateOpt.get());
                return;
            }
        } else {
            to = Long.MAX_VALUE;
        }
        Optional<String> fromAgeOpt = Exchange.queryParams().queryParam(exchange,"fromAge");
        final Integer fromAge;
        if (fromAgeOpt.isPresent()) {
            fromAge = getInteger(fromAgeOpt.get(), 0);
            if (fromAge == null) {
                Exchange.error().badRequest(exchange,  "Bad fromAge format " + fromAgeOpt.get());
                return;
            }
        } else {
            fromAge = 0;
        }

        Optional<String> toAgeOpt = Exchange.queryParams().queryParam(exchange,"toAge");
        final Integer toAge;
        if (toAgeOpt.isPresent()) {
            toAge = getInteger(toAgeOpt.get(), 0);
            if (toAge == null) {
                Exchange.error().badRequest(exchange,  "Bad toAge format " + toAgeOpt.get());
                return;
            }
        } else {
            toAge = Integer.MAX_VALUE;
        }
        Optional<String> genderOpt = Exchange.queryParams().queryParam(exchange,"gender");
        final Gender gender;
        if (genderOpt.isPresent()) {
            gender = Utils.getGenderOrDefault(genderOpt.get(), null);
            if (gender == null) {
                Exchange.error().badRequest(exchange,  "Bad gender format " + genderOpt.get());
                return;
            }
        } else {
            gender = null;
        }
;
        double avg = visitRepository.findByLocationId(location.id).stream().filter(v -> {

            if (v.visited_at < from || to < v.visited_at) {
                return false;
            }

            User user = userRepository.findById(v.user);

            if (user == null) {
                throw new RuntimeException();
            }

            Integer age = Utils.getAge(user.birth_date);

            if (age <= fromAge || age >= toAge) {
                return false;
            }

            if (gender != null && !gender.equals(user.gender)) {
                return false;
            }

            return true;
        }).collect(Collectors.averagingDouble(value -> (double) value.mark));

        Avg avgContainer = new Avg(avg);
        Exchange.body().sendJson(exchange, avgContainer);
    }

    public class Avg {
        public Double avg;

        public Avg(double avg) {
//            this.avg = (double) Math.round (avg * 100000.0) / 100000.0;  ;
            this.avg = BigDecimal.valueOf(avg)
                    .setScale(5, RoundingMode.HALF_UP)
                    .doubleValue();;
        }
    }

}
