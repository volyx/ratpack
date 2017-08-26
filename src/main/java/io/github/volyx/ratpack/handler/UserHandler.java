package io.github.volyx.ratpack.handler;

import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.model.VisitList;
import io.github.volyx.ratpack.model.VisitPlace;
import io.github.volyx.ratpack.repository.UserRepository;
import io.github.volyx.ratpack.repository.VisitRepository;
import io.github.volyx.ratpack.update.UserUpdate;
import io.github.volyx.ratpack.utils.Utils;
import io.github.volyx.ratpack.validate.UserValidator;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class UserHandler {
    private final UserRepository userRepository;
    private final VisitRepository visitRepository;
    private final UserValidator validator = new UserValidator();

    public UserHandler(@Nonnull UserRepository userRepository, @Nonnull VisitRepository visitRepository) {
        this.userRepository = userRepository;
        this.visitRepository = visitRepository;
    }

    public void get(@Nonnull HttpServerExchange exchange) {
        PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        String idParam = pathMatch.getParameters().get("id");
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            Exchange.error().notFound(exchange, "Not found " + idParam);
            return;
        }
        @Nullable User user = userRepository.findById(id);
        if (user == null) {
            Exchange.error().notFound(exchange, "Not found user by" + idParam);
            return;
        }
        Exchange.body().sendJson(exchange, user);
    }

    public void update(@Nonnull HttpServerExchange exchange) {
        PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        String idParam = pathMatch.getParameters().get("id");
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            Exchange.error().notFound(exchange, "Bad format id " + idParam);
            return;
        }
        @Nullable User user = userRepository.findById(id);
        if (user == null) {
            Exchange.error().notFound(exchange, "Not found " + idParam);
            return;
        }

       UserUpdate update = Exchange.body().parseJson(exchange, UserUpdate.class);
        if (update == null) {
            Exchange.error().badRequest(exchange, "Update is null " + user.id);
            return;
        }

        if (!Utils.isNullOrEmpty(update.last_name)) {
            user.last_name = update.last_name;
        }
        if (!Utils.isNullOrEmpty(update.email)) {
            user.email = update.email;
        }
        if (!Utils.isNullOrEmpty(update.first_name)) {
            user.first_name = update.first_name;
        }
        if (update.birth_date != null) {
            user.birth_date = update.birth_date;
        }
        if (update.gender != null) {
            user.gender = update.gender;
        }
        userRepository.save(user);
        Exchange.body().sendEmptyJson(exchange);
    }

    public void create(@Nonnull HttpServerExchange exchange) {
        User user = Exchange.body().parseJson(exchange, User.class);
        validator.validateNew(user);
        userRepository.save(user);
        Exchange.body().sendEmptyJson(exchange);
    }

    public void getVisits(@Nonnull HttpServerExchange exchange) {
        PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        String idParam = pathMatch.getParameters().get("id");
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            Exchange.error().badRequest(exchange, "Bad id " + idParam);
            return;
        }

        User user = userRepository.findById(id);

        if (user == null) {
            Exchange.error().notFound(exchange, "Not found user by id " + id);
            return;
        }

        Optional<String> fromDateOpt =  Exchange.queryParams().queryParam(exchange, "fromDate");
        Optional<String> toDateOpt = Exchange.queryParams().queryParam(exchange, "toDate");
        Optional<String> countryOpt = Exchange.queryParams().queryParam(exchange, "country");
        Optional<String> toDistanceOpt = Exchange.queryParams().queryParam(exchange, "toDistance");
        Integer distance = null;
        if (toDistanceOpt.isPresent()) {
            distance = Utils.getIntegerOrDefault(toDistanceOpt.get(), null);
            if (distance == null) {
                Exchange.error().badRequest(exchange,  "Distance format " + toDistanceOpt.get());
                return;
            }
        }
        Long from = null;
        if (fromDateOpt.isPresent()) {
            from = Utils.getLongOrDefault(fromDateOpt.get(), null);
            if (from == null) {
                Exchange.error().badRequest(exchange,  "From format " + fromDateOpt.get());
                return;
            }
        }
        Long to = null;
        if (toDateOpt.isPresent()) {
            to = Utils.getLongOrDefault(toDateOpt.get(), null);
            if (to == null) {
                Exchange.error().badRequest(exchange,  "To format " + toDateOpt.get());
                return;
            }
        }
        String country = countryOpt.orElse(null);
        final List<VisitPlace> visits = visitRepository.findVisits(id, from, to, country, distance);
        Exchange.body().sendJson(exchange, new VisitList(visits));
    }
}