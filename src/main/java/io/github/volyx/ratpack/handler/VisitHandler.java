package io.github.volyx.ratpack.handler;

import co.cask.http.AbstractHttpHandler;
import co.cask.http.HttpResponder;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.repository.VisitRepository;
import io.github.volyx.ratpack.validate.VisitValidator;
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


public class VisitHandler extends AbstractHttpHandler {

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);
    private final VisitRepository visitRepository;
    private final Gson gson;
    private final VisitValidator validator = new VisitValidator();

    public VisitHandler(@Nonnull VisitRepository visitRepository, @Nonnull Gson gson) {
        this.visitRepository = visitRepository;
        this.gson = gson;
    }

    @Path("/visits/{id}")
    @GET
    public void get(HttpRequest request, HttpResponder responder, @PathParam("id") String idParam) {
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Not found " + idParam);
            return;
        }

        @Nullable Visit visit = visitRepository.findById(id);
        if (visit == null) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Not found " + idParam);
            return;
        }
        responder.sendJson(HttpResponseStatus.OK, visit);
    }

    @Path("/visits/{id}")
    @POST
    public void update(HttpRequest request, HttpResponder responder, @PathParam("id") String idParam) {
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Not found " + idParam);
            return;
        }
        @Nullable Visit visit = visitRepository.findById(id);
        if (visit == null) {
            responder.sendString(HttpResponseStatus.NOT_FOUND, "Not found " + idParam);
            return;
        }

        String body = request.getContent().toString(Charsets.UTF_8);
        String violations = validator.validateJson(body);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Validation " + violations);
            return;
        }
        Visit update = gson.fromJson(body, Visit.class);
        violations = validator.validateUpdate(update);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Validation " + violations);
            return;
        }
        if (update.location != null) {
            visit.location = update.location;
        }
        if (update.mark != null) {
            visit.mark = update.mark;
        }
        if (update.user != null) {
            visit.user = update.user;
        }
        if (update.visited_at != null) {
            visit.visited_at = update.visited_at;
        }
        if (update.visited_at != null) {
            visit.visited_at = update.visited_at;
        }
        visitRepository.update(visit);
        responder.sendJson(HttpResponseStatus.OK, "{}");
    }


    @Path("/visits/new")
    @POST
    public void create(HttpRequest request, HttpResponder responder) {
        String body = request.getContent().toString(Charsets.UTF_8);
        String violations = validator.validateJson(body);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Validation " + violations);
            return;
        }
        Visit visit = gson.fromJson(body, Visit.class);
        violations = validator.validateNew(visit);
        if (!violations.isEmpty()) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Validation " + violations);
            return;
        }
        if (visit.id == null) {
            responder.sendString(HttpResponseStatus.BAD_REQUEST, "Visit id is empty");
            return;
        }
        visitRepository.save(visit);
        responder.sendJson(HttpResponseStatus.OK, "{}");
    }
}
