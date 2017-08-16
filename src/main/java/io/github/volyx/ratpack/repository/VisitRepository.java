package io.github.volyx.ratpack.repository;

import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.storage.Storage;
import io.github.volyx.ratpack.storage.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.volyx.ratpack.Main.conf;

public class VisitRepository {
    @Nonnull
    private final Storage storage;
    private Type type;

    public VisitRepository(@Nonnull Storage storage) {
        this.storage = storage;
        this.type = Type.visit;
    }

    @Nonnull
    public Visit save(@Nonnull Visit visit) {
        storage.put(type, conf.asByteArray(visit.id), conf.asByteArray(visit));
        return visit;
    }

    public void save(@Nonnull List<Visit> visits) {
        Map<byte[], byte[]> batch = new HashMap<>(visits.size());
        for (Visit v : visits) {
            batch.put(conf.asByteArray(v.id), conf.asByteArray(v));
        }
        type = Type.visit;
        storage.bulk(type, batch);
    }

    @Nullable
    public Visit findById(@Nonnull Integer id) {
        byte[] bytes = storage.get(Type.visit, conf.asByteArray(id));
        if (bytes != null) {
            return (Visit) conf.asObject(bytes);
        }
        return null;
    }

    @Nonnull
    public Visit update(Visit visit) {
        storage.put(type, conf.asByteArray(visit.id), conf.asByteArray(visit));
        return visit;
    }

    public List<Visit> findAll() {
        List<byte[]> all = storage.findAll(type);
        List<Visit> visits = new ArrayList<>(all.size());
        for (byte[] bytes : all) {
            visits.add((Visit) conf.asObject(bytes));
        }
        return visits;
    }
}