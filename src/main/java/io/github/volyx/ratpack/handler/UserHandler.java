package io.github.volyx.ratpack.handler;

import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import io.github.volyx.ratpack.Main;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.model.VisitList;
import io.github.volyx.ratpack.model.VisitPlace;
import io.github.volyx.ratpack.repository.Repository;
import io.github.volyx.ratpack.update.UserUpdate;
import io.github.volyx.ratpack.validate.UserValidator;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.codahale.metrics.MetricRegistry.name;

public class UserHandler {
    private final Repository repo;
    private final UserValidator validator = new UserValidator();
    private final Timer getTimer;
    private final Timer updateTimer;
    private final Timer createTimer;
    private final Timer visitsTimer;

    public UserHandler(@Nonnull Repository userRepository) {
        this.repo = userRepository;
        this.getTimer = Main.metricRegistry.timer(name(UserHandler.class, "get"));
        this.createTimer = Main.metricRegistry.timer(name(UserHandler.class, "create"));
        this.updateTimer = Main.metricRegistry.timer(name(UserHandler.class, "update"));
        this.visitsTimer = Main.metricRegistry.timer(name(UserHandler.class, "visits"));
    }

    public void get(@Nonnull HttpServerExchange exchange) {
        final Timer.Context context = getTimer.time();
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
            @Nullable User user = repo.findById(id, User.class);
            if (user == null) {
                Exchange.error().notFound(exchange, "Not found user by" + idParam);
                return;
            }
            Exchange.body().sendJson(exchange, user);
        } finally {
            context.stop();
        }
    }

    public void update(@Nonnull HttpServerExchange exchange) {
        final Timer.Context context = updateTimer.time();
        try {
            // handle request

            PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
            String idParam = pathMatch.getParameters().get("id");
            Integer id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                Exchange.error().notFound(exchange, "Bad format id " + idParam);
                return;
            }
            @Nullable User user = repo.findById(id, User.class);
            if (user == null) {
                Exchange.error().notFound(exchange, "Not found " + idParam);
                return;
            }

            UserUpdate update = Exchange.body().parseJson(exchange, UserUpdate.class);
            if (update == null) {
                Exchange.error().badRequest(exchange, "Update is null " + user.id);
                return;
            }

            if (update.last_name != null) {
                user.last_name = update.last_name;
            }
            if (update.email != null) {
                user.email = update.email;
            }
            if (update.first_name != null) {
                user.first_name = update.first_name;
            }
            if (update.birth_date != null) {
                user.birth_date = update.birth_date;
            }
            if (update.gender != null) {
                user.gender = update.gender;
            }
            repo.save(user);
            Exchange.body().sendEmptyJson(exchange);
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
        } finally {
            context.stop();
        }
    }

    public void create(@Nonnull HttpServerExchange exchange) {
        final Timer.Context context = createTimer.time();
        try {

            User user = Exchange.body().parseJson(exchange, User.class);
            validator.validateNew(user);
            repo.save(user);
            Exchange.body().sendEmptyJson(exchange);     // handle request
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
        } finally {
            context.stop();
        }
    }

    public void getVisits(@Nonnull HttpServerExchange exchange) {
        final Timer.Context context = visitsTimer.time();
        try {
            // handle request

            PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
            String idParam = pathMatch.getParameters().get("id");
            Integer id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                Exchange.error().badRequest(exchange, "Bad id " + idParam);
                return;
            }

            User user = repo.findById(id, User.class);

            if (user == null) {
                Exchange.error().notFound(exchange, "Not found user by id " + id);
                return;
            }

            Optional<String> fromDateOpt = Exchange.queryParams().queryParam(exchange, "fromDate");
            Optional<String> toDateOpt = Exchange.queryParams().queryParam(exchange, "toDate");
            Optional<String> countryOpt = Exchange.queryParams().queryParam(exchange, "country");
            Optional<String> toDistanceOpt = Exchange.queryParams().queryParam(exchange, "toDistance");
//            System.out.println(fromDateOpt + " " + toDateOpt + " " + countryOpt + " " + toDistanceOpt);
            @Nullable final Integer distance;
            if (toDistanceOpt.isPresent()) {
                try {
                    distance = Integer.parseInt(toDistanceOpt.get());
                } catch (NumberFormatException ignored) {
                    Exchange.error().badRequest(exchange, "Distance format " + toDistanceOpt.get());
                    return;
                }
            } else {
                distance = null;
            }
            @Nullable final Long from;
            if (fromDateOpt.isPresent()) {
                try {
                    from = Long.parseLong(fromDateOpt.get());
                } catch (NumberFormatException ignored) {
                    Exchange.error().badRequest(exchange, "From format " + fromDateOpt.get());
                    return;
                }
            } else {
                from = null;
            }
            @Nullable final Long to;
            if (toDateOpt.isPresent()) {
                try {
                    to = Long.parseLong(toDateOpt.get());
                } catch (NumberFormatException ignored) {
                    Exchange.error().badRequest(exchange, "To format " + toDateOpt.get());
                    return;
                }
            } else {
                to = null;
            }
            String country = countryOpt.orElse(null);
//            System.out.println(id + " " + from + " " + to + " " + country + " " + distance);
            final List<VisitPlace> visits = repo.findVisits(id, from, to, country, distance);
            Exchange.body().sendJson(exchange, new VisitList(visits));
        } finally {
            context.stop();
        }
    }
}