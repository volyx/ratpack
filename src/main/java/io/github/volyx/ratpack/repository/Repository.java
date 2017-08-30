package io.github.volyx.ratpack.repository;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.model.VisitPlace;
import io.github.volyx.ratpack.update.VisitUpdate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Repository {

    private TIntObjectHashMap<User> users = new TIntObjectHashMap<User>(1_100_000);

    private TIntObjectHashMap<Location> locations = new TIntObjectHashMap<Location>(1_100_000);

    private TIntObjectHashMap<Visit> visits = new TIntObjectHashMap<Visit>(11_000_000);

    private Multimap<Integer, Integer> userToVisits = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
    private Multimap<Integer, Integer> locationToVisits = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    public void save(@Nonnull Visit visit) {
        visits.put(visit.id, visit);
        locationToVisits.put(visit.location, visit.id);
        userToVisits.put(visit.user, visit.id);
    }

    public void save(@Nonnull User user) {
        users.put(user.id, user);
    }

    public void save(@Nonnull Location location) {
        locations.put(location.id, location);
    }

    public void save(@Nonnull TIntObjectHashMap<User> users) {
       this.users.putAll(users);
    }

    @Nullable
    public <T> T findById(@Nonnull Integer id, Class<T> clazz) {
        if (clazz.equals(User.class)) {
            return (T) this.users.get(id);
        }
        if (clazz.equals(Location.class)) {
            return (T) this.locations.get(id);
        }
        if (clazz.equals(Visit.class)) {
            return (T) this.visits.get(id);
        }
        throw new UnsupportedOperationException();
    }

    public void save(@Nonnull Location[] locations) {
        for (Location location : locations) {
            save(location);
        }
    }

    public void save(@Nonnull Visit[] visits) {
        for (Visit visit : visits) {
            save(visit);
        }
    }

    public List<VisitPlace> findVisits(@Nonnull Integer id, @Nullable Long from, @Nullable Long to, @Nullable String country, @Nullable Integer distance) {
        Collection<Integer> visits = userToVisits.get(id);
        if (visits == null || visits.isEmpty()) {
            return Collections.emptyList();
        }
        Visit v;
        List<VisitPlace> places = new ArrayList<>(visits.size());
        for (Object visit : visits) {
            Integer visitId = (Integer) visit;
            v = findById(visitId, Visit.class);

            if (v == null) {
                throw new RuntimeException();
            }

            if (from != null && v.visited_at <= from) {
                continue;
            }
            if (to != null && to <= v.visited_at) {
                continue;
            }
            Location location = null;
            if (country != null) {
                location = findById(v.location, Location.class);
                if (location != null) {
                    if (!country.equals(location.country)) {
                        continue;
                    }
                }
            }
            if (distance != null) {
                location = findById(v.location, Location.class);
                if (location != null) {
                    if (location.distance >= distance) {
                        continue;
                    }
                }
            }
            VisitPlace visitPlace = new VisitPlace();
            visitPlace.mark = v.mark;
            visitPlace.visited_at = v.visited_at;
            visitPlace.place = (location != null) ? location.place : findById(v.location, Location.class).place;
            places.add(visitPlace);

        }

        Comparator<VisitPlace> comparator = Comparator.comparingLong(o -> o.visited_at);
        places.sort(comparator);
        return places;
    }

    public Collection<Visit> findByLocationId(@Nonnull Integer locationId) {
        Collection<Integer> visits = locationToVisits.get(locationId);
        if (visits == null) {
            return Collections.emptyList();
        }
        List<Visit> result = new ArrayList<>(visits.size());
        for (Object v : visits) {
            Integer visitId  = (Integer) v;
            Visit visit = findById(visitId, Visit.class);
            if (visit == null) {
                throw new RuntimeException();
            }
            result.add(visit);
        }
        return result;
    }

    public void update(@Nonnull Visit visit, @Nonnull VisitUpdate update) {
        if (update.location != null) {
            locationToVisits.get(visit.location).remove(visit.id);
            visit.location = update.location;
        }
        if (update.mark != null) {
            visit.mark = update.mark;
        }
        if (update.user != null) {
            userToVisits.get(visit.user).remove(visit.id);
            visit.user = update.user;
        }
        if (update.visited_at != null) {
//            repository.saveVisitAt(visit);
            visit.visited_at = update.visited_at;
        }
        save(visit);
    }
}