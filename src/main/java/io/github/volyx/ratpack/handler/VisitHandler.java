package io.github.volyx.ratpack.handler;

import co.cask.http.AbstractHttpHandler;
import co.cask.http.HttpResponder;
import com.google.gson.Gson;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.repository.VisitRepository;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Validator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

public class VisitHandler extends AbstractHttpHandler {

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);
    private final VisitRepository visitRepository;
    private final Validator validator;
    private final Gson gson;

    public VisitHandler(@Nonnull VisitRepository visitRepository, @Nonnull Validator validator, @Nonnull Gson gson) {
        this.visitRepository = visitRepository;
        this.validator = validator;
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

}
