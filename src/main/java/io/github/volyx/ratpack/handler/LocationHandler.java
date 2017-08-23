package io.github.volyx.ratpack.handler;

import co.cask.http.AbstractHttpHandler;
import co.cask.http.HttpResponder;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import io.github.volyx.ratpack.model.Gender;
import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.repository.LocationRepository;
import io.github.volyx.ratpack.repository.UserRepository;
import io.github.volyx.ratpack.repository.VisitRepository;
import io.github.volyx.ratpack.utils.Utils;
import io.github.volyx.ratpack.validate.LocationValidator;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.volyx.ratpack.handler.UserHandler.OBJECT;
import static io.github.volyx.ratpack.utils.Utils.getInteger;
import static io.github.volyx.ratpack.utils.Utils.getLong;

public class LocationHandler extends AbstractHttpHandler {

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);
    private final LocationRepository locationRepository;
    private final VisitRepository visitRepository;
    private final UserRepository userRepository;
    private final LocationValidator validator = new LocationValidator();
    private final Gson gson;

    public LocationHandler(@Nonnull LocationRepository locationRepository, @Nonnull VisitRepository visitRepository, @Nonnull UserRepository userRepository, @Nonnull Gson gson) {
        this.locationRepository = locationRepository;
        this.visitRepository = visitRepository;
        this.userRepository = userRepository;
        this.gson = gson;
    }

    @Path("/locations/{id}")
    @GET
    public void get(HttpRequest request, HttpResponder responder, @PathParam("id") String idParam) {
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Not found " + idParam);
            return;
        }

        @Nullable Location location = locationRepository.findById(id);
        if (location == null) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Not found " + idParam);
            return;
        }
        responder.sendJson(HttpResponseStatus.OK, location);
    }

    @Path("/locations/{id}")
    @POST
    public void update(HttpRequest request, HttpResponder responder, @PathParam("id") String idParam) {
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Not found " + idParam);
            return;
        }
        @Nullable Location location = locationRepository.findById(id);
        if (location == null) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Not found " + idParam);
            return;
        }
        String body = request.getContent().toString(Charsets.UTF_8);
        String violations = validator.validateJson(body);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Validation " + violations);
            return;
        }
        Location update = gson.fromJson(body, Location.class);
        violations = validator.validateUpdate(update);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Validation " + violations);
            return;
        }
        location.id = id;
        if (!Strings.isNullOrEmpty(update.place )) {
            location.place = update.place;
        }
        if (!Strings.isNullOrEmpty(update.country )) {
            location.country = update.country;
        }
        if (!Strings.isNullOrEmpty(update.city )) {
            location.city = update.city;
        }
        if (update.distance != null) {
            location.distance = update.distance;
        }
        locationRepository.save(location);
        responder.sendJson(HttpResponseStatus.OK, OBJECT);
    }

    @Path("/locations/new")
    @POST
    public void create(HttpRequest request, HttpResponder responder) {
        String body = request.getContent().toString(Charsets.UTF_8);
        String violations = validator.validateJson(body);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Validation " + violations);
            return;
        }
        Location location = gson.fromJson(body, Location.class);
        violations = validator.validateNew(location);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Validation " + violations);
            return;
        }
        locationRepository.save(location);
        responder.sendJson(HttpResponseStatus.OK, OBJECT);
    }

    @Path("/locations/{id}/avg")
    @GET
    public void avg(HttpRequest request, HttpResponder responder, @PathParam("id") String idParam) {
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Bad id format " + idParam);
            return;
        }

        Location location = locationRepository.findById(id);
        if (location == null) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Not found location " + idParam);
            return;
        }


        Map<String, String> queryMap = new HashMap<>(5);
        try {
            String query = URL.parse("http://localhost" + request.getUri()).query();
            if (query != null) {
                queryMap.putAll(Splitter.on('&').withKeyValueSeparator("=").split(query));
            }
        } catch (GalimatiasParseException e) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Bad id format " + idParam);
            return;
        }


        String fromDate = queryMap.get("fromDate");
        final Long from = getLong(fromDate, 0L);
        if (from == null) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Bad from format " + fromDate);
            return;
        }

        String toDate = queryMap.get("toDate");

        final Long to = getLong(toDate, Long.MAX_VALUE);
        if (to == null) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Bad to format " + toDate);
            return;
        }

        String fromAgeParam = queryMap.get("fromAge");
        final Integer fromAge = getInteger(fromAgeParam, 0);
        if (fromAge == null) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Bad fromAge format " + fromAgeParam);
            return;
        }
        String toAgeParam = queryMap.get("toAge");
        final Integer toAge = getInteger(toAgeParam, Integer.MAX_VALUE);

        if (toAge == null) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Bad toAge format " + toAgeParam);
            return;
        }

        String genderParam = queryMap.get("gender");
        Gender gender;
        if (genderParam != null) {
            gender = Utils.getGenderOrDefault(genderParam, null);
            if (gender == null) {
                responder.sendString(HttpResponseStatus.BAD_REQUEST, "Bad gender format " + genderParam);
                return;
            }
        } else {
            gender = null;
        }

        Collection<Visit> allVisits = visitRepository.findAll();

        Double avg = allVisits.stream().filter(v -> {
            if (!v.location.equals(id)) {
                return false;
            }

            if (v.visited_at > to || v.visited_at < from) {
                return false;
            }

            User user = userRepository.findById(v.user);

            if (user == null) {
                return false;
            }

            Integer age = Utils.getAge(user.birth_date);

            if (age <= fromAge || age >= toAge) {
                return false;
            }

            if (gender != null && !gender.equals(user.gender)) {
                return false;
            }
            return true;
        })
                .collect(Collectors.averagingDouble(value -> (double) value.mark));
        Avg avgContainer = new Avg(avg);
        responder.sendJson(HttpResponseStatus.OK, avgContainer);
    }

    public class Avg {
        public Double avg;

        public Avg(Double avg) {
            this.avg = avg;
        }
    }

}
