package io.github.volyx.ratpack.repository;

import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.model.VisitPlace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class VisitRepository {
    private Map<Integer, Visit> visits = new ConcurrentHashMap<>(10000);
    @Nonnull
    private final LocationRepository locationRepository;

    public VisitRepository(@Nonnull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }


    public void save(@Nonnull Visit visit) {
        visits.put(visit.id, visit);
    }

    public void save(@Nonnull List<Visit> visits) {
        for (Visit visit : visits) {
            save(visit);
        }
    }

    @Nullable
    public Visit findById(@Nonnull Integer id) {
        return visits.get(id);
    }

    public Collection<Visit> findAll() {
        return visits.values();
    }

    public List<VisitPlace> findVisits(@Nonnull Integer id, @Nonnull Long from, @Nonnull Long to, @Nullable String country, @Nullable Integer distance) {

        return findAll().stream()
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