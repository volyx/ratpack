package io.github.volyx.ratpack.handler;

import co.cask.http.AbstractHttpHandler;
import co.cask.http.HttpResponder;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.model.VisitPlace;
import io.github.volyx.ratpack.repository.UserRepository;
import io.github.volyx.ratpack.repository.VisitRepository;
import io.github.volyx.ratpack.utils.Utils;
import io.github.volyx.ratpack.validate.UserValidator;
import io.mola.galimatias.GalimatiasParseException;
import io.mola.galimatias.URL;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class UserHandler extends AbstractHttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);
    private final UserRepository userRepository;
    private final VisitRepository visitRepository;
    private final UserValidator validator = new UserValidator();
    private final Gson gson;

    public UserHandler(@Nonnull UserRepository userRepository, @Nonnull VisitRepository visitRepository, @Nonnull Gson gson) {
        this.userRepository = userRepository;
        this.visitRepository = visitRepository;
        this.gson = gson;
    }

    @Path("/users/{id}")
    @GET
    public void get(HttpRequest request, HttpResponder responder, @PathParam("id") String idParam) {
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Not found " + idParam);
            return;
        }
        @Nullable User user = userRepository.findById(id);
        if (user == null) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Not found " + idParam);
            return;
        }
        responder.sendJson(HttpResponseStatus.OK, user);
    }

    @Path("/users/{id}")
    @POST
    public void update(HttpRequest request, HttpResponder responder, @PathParam("id") String idParam) {
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Bad format id " + idParam);
            return;
        }
        @Nullable User user = userRepository.findById(id);
        if (user == null) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Not found " + idParam);
            return;
        }

        String body = request.getContent().toString(Charsets.UTF_8);
        String violations = validator.validateJson(body);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Validation " + violations);
            return;
        }
        User update = gson.fromJson(body, User.class);
        if (update.id != null) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Id should not exist " + user.id);
            return;
        }

        violations = validator.validateUpdate(update);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Validation " + violations);
            return;
        }
        if (!Strings.isNullOrEmpty(update.last_name)) {
            user.last_name = update.last_name;
        }
        if (!Strings.isNullOrEmpty(update.email)) {
            user.email = update.email;
        }
        if (!Strings.isNullOrEmpty(update.first_name)) {
            user.first_name = update.first_name;
        }
        if (update.birth_date != null) {
            user.birth_date = update.birth_date;
        }
        userRepository.save(user);
        responder.sendJson(HttpResponseStatus.OK, "{}");
    }

    @Path("/users/new")
    @POST
    public void create(HttpRequest request, HttpResponder responder) {
        String body = request.getContent().toString(Charsets.UTF_8);
        String violations = validator.validateJson(body);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Validation " + violations);
            return;
        }
        User user = gson.fromJson(body, User.class);
        violations = validator.validateNew(user);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Validation " + violations);
            return;
        }
        if (user.id == null) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "User id is empty");
            return;
        }
        userRepository.save(user);
        responder.sendJson(HttpResponseStatus.OK, "{}");
    }

    /**
     *
     * fromDate - посещения с visited_at > fromDate
     * toDate - посещения с visited_at < toDate
     * country - название страны, в которой находятся интересующие достопримечательности
     * toDistance - возвращать только те места, у которых расстояние от города меньше этого параметра
     *
     */
    @Path("/users/{id}/visits")
    @GET
    public void getVisits(HttpRequest request, HttpResponder responder, @PathParam("id") String idParam) {
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Bad id " + idParam);
            return;
        }

        User user = userRepository.findById(id);

        if (user == null) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Not found user by id " + id);
            return;
        }

        Map<String, String> queryMap = new HashMap<>(5);
        try {
            String query = URL.parse("http://localhost" + request.getUri()).query();
            if (!Strings.isNullOrEmpty(query)) {
                queryMap.putAll(Splitter.on('&').withKeyValueSeparator("=").split(query));
            }
        } catch (GalimatiasParseException e) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Bad id format " + idParam);
            return;
        }

        @Nullable String fromDate = queryMap.get("fromDate");
        @Nullable String toDate = queryMap.get("toDate");
        @Nullable String country = queryMap.get("country");
        @Nullable String toDistance = queryMap.get("toDistance");
        Integer distance = null;
        if (toDistance != null) {
            distance = Utils.getIntegerOrDefault(toDistance, null);
            if (distance == null) {
                responder.sendString(HttpResponseStatus.BAD_REQUEST, "Distance format " + toDistance);
                return;
            }
        }

        if (country != null) {
            country = decode(country);
        }

        Long from = 0L;
        if (fromDate != null) {
            from = Utils.getLongOrDefault(fromDate, null);
            if (from == null) {
                responder.sendString(HttpResponseStatus.BAD_REQUEST, "From format " + fromDate);
                return;
            }
        }
        Long to = Long.MAX_VALUE;
        if (toDate != null) {
            to = Utils.getLongOrDefault(toDate, null);
            if (to == null) {
                responder.sendString(HttpResponseStatus.BAD_REQUEST, "To format " + toDate);
                return;
            }
        }
        final List<VisitPlace> visits = visitRepository.findVisits(id, from, to, country, distance);
        responder.sendJson(HttpResponseStatus.OK, new VisitList(visits));
    }

    private static String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, Charsets.UTF_8.name());
        } catch(final UnsupportedEncodingException e) {
            throw new RuntimeException("Impossible: UTF-8 is a required encoding", e);
        }
    }

    public class VisitList {
        public List<VisitPlace> visits;

        public VisitList(List<VisitPlace> visits) {
            this.visits = visits;
        }
    }
}