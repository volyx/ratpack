package io.github.volyx.ratpack.handler;

import co.cask.http.AbstractHttpHandler;
import co.cask.http.HttpResponder;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.repository.LocationRepository;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Set;

public class LocationHandler extends AbstractHttpHandler {

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);
    private final LocationRepository locationRepository;
    private final Validator validator;
    private final Gson gson;

    public LocationHandler(@Nonnull LocationRepository locationRepository, @Nonnull Validator validator, @Nonnull Gson gson) {
        this.locationRepository = locationRepository;
        this.validator = validator;
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
    public void update(HttpRequest request, HttpResponder responder, @PathParam("id") String id) {
        String body = request.getContent().toString(Charsets.UTF_8);
        Location location = gson.fromJson(body, Location.class);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Validation " + violations);
            return;
        }
        responder.sendJson(HttpResponseStatus.OK, locationRepository.update(location));
    }

    @Path("/locations/{id}/new")
    @POST
    public void create(HttpRequest request, HttpResponder responder, @PathParam("id") String id) {
        String body = request.getContent().toString(Charsets.UTF_8);
        Location location = gson.fromJson(body, Location.class);
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Validation " + violations);
            return;
        }
        responder.sendJson(HttpResponseStatus.OK, locationRepository.save(location));
    }

}
