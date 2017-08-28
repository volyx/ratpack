package io.github.volyx.ratpack.handler;

import com.codahale.metrics.Timer;
import io.github.volyx.ratpack.Main;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.repository.Repository;
import io.github.volyx.ratpack.update.VisitUpdate;
import io.github.volyx.ratpack.validate.VisitValidator;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.codahale.metrics.MetricRegistry.name;


public class VisitHandler {
    private final Repository repository;
    private final VisitValidator validator = new VisitValidator();
    private final Timer getTimer;
    private final Timer createTimer;
    private final Timer updateTimer;

    public VisitHandler(@Nonnull Repository repository) {
        this.repository = repository;
        this.getTimer = Main.metricRegistry.timer(name(VisitHandler.class, "get"));
        this.createTimer = Main.metricRegistry.timer(name(VisitHandler.class, "create"));
        this.updateTimer = Main.metricRegistry.timer(name(VisitHandler.class, "update"));
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
            @Nullable Visit visit = repository.findById(id, Visit.class);
            if (visit == null) {
                Exchange.error().notFound(exchange, "Not found visit by" + idParam);
                return;
            }
            Exchange.body().sendJson(exchange, visit);
        } finally {
            context.stop();
        }
    }

    public void update(HttpServerExchange exchange) {
        final Timer.Context context = updateTimer.time();
        try {
            PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
            String idParam = pathMatch.getParameters().get("id");
            Integer id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                Exchange.error().notFound(exchange, "Bad format " + idParam);
                return;
            }
            @Nullable Visit visit = repository.findById(id, Visit.class);
            if (visit == null) {
                Exchange.error().notFound(exchange, "Not found " + idParam);
                return;
            }
            VisitUpdate update = Exchange.body().parseJson(exchange, VisitUpdate.class);
            if (update == null) {
                Exchange.error().badRequest(exchange, "Update is null " + visit.id);
                return;
            }
            repository.update(visit, update);
            Exchange.body().sendEmptyJson(exchange);
        } finally {
            context.stop();
        }
    }

    public void create(HttpServerExchange exchange) {
        final Timer.Context context = createTimer.time();
        try {
            Visit visit = Exchange.body().parseJson(exchange, Visit.class);
            validator.validateNew(visit);
            repository.save(visit);
            Exchange.body().sendEmptyJson(exchange);
        } finally {
            context.stop();
        }
    }
}
