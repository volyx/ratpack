package io.github.volyx.ratpack.repository;

import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.storage.Storage;
import io.github.volyx.ratpack.storage.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.volyx.ratpack.Main.conf;

public class LocationRepository {
    private final Storage storage;

    public LocationRepository(@Nonnull Storage storage) {
        this.storage = storage;

    }

    @Nullable
    public Location findById(Integer id) {
        byte[] bytes = storage.get(Type.location, conf.asByteArray(id));
        if (bytes == null) {
            return null;
        }
        return (Location) conf.asObject(bytes);
    }

    public Location update(Location location) {
        storage.put(Type.location, conf.asByteArray(location.id), conf.asByteArray(location));
        return location;
    }

    public Location save(@Nonnull Location location) {
        storage.put(Type.location, conf.asByteArray(location.id), conf.asByteArray(location));
        return location;
    }

    public void save(@Nonnull List<Location> locations) {
        Map<byte[], byte[]> batch = new HashMap<>(locations.size());
        for (Location l : locations) {
            batch.put(conf.asByteArray(l.id), conf.asByteArray(l));
        }
        storage.bulk(Type.location, batch);
    }

    public List<Location> findAll() {
        List<byte[]> all = storage.findAll(Type.location);
        List<Location> locations = new ArrayList<>(all.size());
        for (byte[] bytes : all) {
            locations.add((Location) conf.asObject(bytes));
        }
        return locations;
    }
}

