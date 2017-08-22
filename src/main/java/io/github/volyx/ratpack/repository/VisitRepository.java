package io.github.volyx.ratpack.repository;

import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.model.VisitPlace;
import io.github.volyx.ratpack.storage.Storage;
import io.github.volyx.ratpack.storage.Type;
import io.github.volyx.ratpack.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.github.volyx.ratpack.Main.conf;

public class VisitRepository {
    @Nonnull
    private final Storage storage;
    private LocationRepository locationRepository;
    private final Type type;

    public VisitRepository(@Nonnull Storage storage, @Nonnull LocationRepository locationRepository) {
        this.storage = storage;
        this.locationRepository = locationRepository;
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

    public List<VisitPlace> findVisits(@Nonnull Integer id, @Nonnull Long from, @Nonnull Long to, @Nullable String country, @Nullable Integer distance) {
        final List<Visit> visits = findAll();

        return visits.stream()
                .filter(v -> {
                    if (!v.user.equals(id)) {
                        return false;
                    }
                    if (v.visited_at > to || v.visited_at < from) {
                        return false;
                    }

                    Location location = null;
                    if (country != null) {
                        location = locationRepository.findById(v.location);
                        if (location != null) {
                            if (!country.equals(location.country)) {
                                return false;
                            }
                        }
                    }

                    if (distance != null) {
                        location = locationRepository.findById(v.location);
                        if (location != null) {
                            if (location.distance >= distance) {
                                return false;
                            }
                        }
                    }
//                    System.out.println(v + ((location != null) ? location.toString() : ""));
                   return true;
                })
                .map(visit -> {
                    VisitPlace visitPlace = new VisitPlace();
                    visitPlace.mark = visit.mark;
                    visitPlace.visited_at = visit.visited_at;
                    visitPlace.place = locationRepository.findById(visit.location).place;
//                    System.out.println(visitPlace.place + " " + visitPlace.visited_at);
                    return visitPlace;
                })
                .sorted(Comparator.comparing(o -> o.visited_at))
                .collect(Collectors.toList());

    }


}