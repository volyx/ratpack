package io.github.volyx.ratpack.repository;

import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.model.VisitPlace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class VisitRepository {
    private final Map<Integer, Visit> visits = new ConcurrentHashMap<>(1000000);
    private final Map<Integer, Set<Integer>> userToVisits = new ConcurrentHashMap<>(1000000);
    private final Map<Integer, Set<Integer>> locationToVisits = new ConcurrentHashMap<>(1000000);
//    private final ConcurrentNavigableMap<Long, Integer> timeToVisit = new ConcurrentSkipListMap<>();
    @Nonnull
    private final LocationRepository locationRepository;


    public VisitRepository(@Nonnull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }


    public void save(@Nonnull Visit visit) {
        visits.put(visit.id, visit);
        locationToVisits.compute(visit.location, addFunction(visit));
        userToVisits.compute(visit.user, addFunction(visit));
//        timeToVisit.put(visit.visited_at, visit.id);
    }

    public void saveLocationToVisit(@Nonnull Visit visit) {
        locationToVisits.compute(visit.location, removeBiFunction(visit));
    }

    public void saveUserToVisit(@Nonnull Visit visit) {
        userToVisits.compute(visit.user, removeBiFunction(visit));
    }

    public void saveVisitAt(@Nonnull Visit visit) {
//        timeToVisit.remove(visit.visited_at, visit.id);
    }

    private BiFunction<Integer, Set<Integer>, Set<Integer>> removeBiFunction(@Nonnull Visit visit) {
        return (integer, sets) -> {
            if (sets != null) {
                sets.remove(visit.id);
            }
            return sets;
        };
    }

    private BiFunction<Integer, Set<Integer>, Set<Integer>> addFunction(@Nonnull Visit visit) {
        return (integer, sets) -> {
            if (sets == null) {
                HashSet<Integer> set = new HashSet<>(10);
                set.add(visit.id);
                return set;
            } else {
                sets.add(visit.id);
            }
            return sets;
        };
    }

    public void save(@Nonnull Visit[] visits) {
        for (Visit visit : visits) {
            save(visit);
        }
    }

    @Nullable
    public Visit findById(@Nonnull Integer id) {
        return visits.get(id);
    }

    public List<VisitPlace> findVisits(@Nonnull Integer id, @Nullable Long from, @Nullable Long to, @Nullable String country, @Nullable Integer distance) {
        Set<Integer> visits = userToVisits.get(id);
        if (visits == null) {
            return Collections.emptyList();
        }
        Visit v;
//        if (from != null && to != null && from < to) {
//            visits.retainAll(this.timeToVisit.subMap(from, to).values());
//        } else if (from != null && to == null) {
//            visits.retainAll(this.timeToVisit.tailMap(from).values());
//        } else if (from == null && to != null) {
//            visits.retainAll(this.timeToVisit.headMap(to).values());
//        }

        List<VisitPlace> places = new ArrayList<>(visits.size());
        for (Integer visitId : visits) {

            v = this.visits.get(visitId);
            if (from != null && v.visited_at < from) {
                continue;
            }

            if (to != null &&  to < v.visited_at) {
                continue;
            }

            Location location = null;
            if (country != null) {
                location = locationRepository.findById(v.location);
                if (location != null) {
                    if (!country.equals(location.country)) {
                        continue;
                    }
                }
            }

            if (distance != null) {
                location = locationRepository.findById(v.location);
                if (location != null) {
                    if (location.distance >= distance) {
                        continue;
                    }
                }
            }
            VisitPlace visitPlace = new VisitPlace();
            visitPlace.mark = v.mark;
            visitPlace.visited_at = v.visited_at;
            visitPlace.place = (location != null) ? location.place : locationRepository.findById(v.location).place;
            places.add(visitPlace);
        }
        Comparator<VisitPlace> comparator = Comparator.comparingLong(o -> o.visited_at);
        places.sort(comparator);
        return places;
    }

    public Collection<Visit> findByLocationId(@Nonnull Integer locationId) {
        Set<Integer> visits = locationToVisits.get(locationId);
        if (visits == null) {
            return Collections.emptyList();
        }
        List<Visit> result = new ArrayList<>(visits.size());
        for (Integer visitId : visits) {
            result.add(this.visits.get(visitId));
        }
        return result;
    }
}