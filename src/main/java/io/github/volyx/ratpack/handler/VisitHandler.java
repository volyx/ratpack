package io.github.volyx.ratpack.handler;

import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.repository.VisitRepository;
import io.github.volyx.ratpack.update.VisitUpdate;
import io.github.volyx.ratpack.validate.VisitValidator;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class VisitHandler {

    private final VisitRepository visitRepository;
    private final VisitValidator validator = new VisitValidator();

    public VisitHandler(@Nonnull VisitRepository visitRepository) {
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
        @Nullable Visit visit = visitRepository.findById(id);
        if (visit == null) {
            Exchange.error().notFound(exchange, "Not found visit by" + idParam);
            return;
        }
        Exchange.body().sendJson(exchange, visit);
    }

    public void update(HttpServerExchange exchange) {
        PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        String idParam = pathMatch.getParameters().get("id");
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            Exchange.error().notFound(exchange, "Bad format " + idParam);
            return;
        }
        @Nullable Visit visit = visitRepository.findById(id);
        if (visit == null) {
            Exchange.error().notFound(exchange, "Not found " + idParam);
            return;
        }

        VisitUpdate update = Exchange.body().parseJson(exchange, VisitUpdate.class);
        if (update == null) {
            Exchange.error().badRequest(exchange, "Update is null " + visit.id);
            return;
        }
        if (update.location != null) {
            visitRepository.saveLocationToVisit(visit);
            visit.location = update.location;
        }
        if (update.mark != null) {
            visit.mark = update.mark;
        }
        if (update.user != null) {
            visitRepository.saveUserToVisit(visit);
            visit.user = update.user;
        }
        if (update.visited_at != null) {
            visitRepository.saveVisitAt(visit);
            visit.visited_at = update.visited_at;
        }
        visitRepository.save(visit);
        Exchange.body().sendEmptyJson(exchange);
    }

    public void create(HttpServerExchange exchange) {
        Visit visit = Exchange.body().parseJson(exchange, Visit.class);
        validator.validateNew(visit);
        visitRepository.save(visit);
        Exchange.body().sendEmptyJson(exchange);
    }
}
