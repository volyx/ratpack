package io.github.volyx.ratpack.repository;

import io.github.volyx.ratpack.model.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocationRepository {
    private Map<Integer, Location> locations = new ConcurrentHashMap<>(10000);
    public void save(@Nonnull Location location) {
        locations.put(location.id, location);
    }

    public void save(@Nonnull List<Location> locations) {
        for (Location location : locations) {
            save(location);
        }
    }

    @Nullable
    public Location findById(@Nonnull Integer id) {
        return locations.get(id);
    }
}

