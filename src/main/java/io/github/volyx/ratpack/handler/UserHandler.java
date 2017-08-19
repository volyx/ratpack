package io.github.volyx.ratpack.handler;

import co.cask.http.AbstractHttpHandler;
import co.cask.http.HttpResponder;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.repository.UserRepository;
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

public class UserHandler extends AbstractHttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);
    private final UserRepository userRepository;
    private final Validator validator;
    private final Gson gson;

    public UserHandler(@Nonnull UserRepository userRepository, @Nonnull Validator validator, @Nonnull Gson gson) {
        this.userRepository = userRepository;
        this.validator = validator;
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
    public void update(HttpRequest request, HttpResponder responder, @PathParam("id") String id) {
        String body = request.getContent().toString(Charsets.UTF_8);
        User user = gson.fromJson(body, User.class);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Validation " + violations);
            return;
        }
        responder.sendJson(HttpResponseStatus.OK, userRepository.update(user));
    }

    @Path("/users/{id}/new")
    @POST
    public void create(HttpRequest request, HttpResponder responder, @PathParam("id") String id) {
        String body = request.getContent().toString(Charsets.UTF_8);
        User user = gson.fromJson(body, User.class);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Validation " + violations);
            return;
        }
        responder.sendJson(HttpResponseStatus.OK, userRepository.save(user));
    }
}