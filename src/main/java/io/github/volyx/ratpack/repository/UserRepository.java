package io.github.volyx.ratpack.repository;


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

public class UserRepository {

    private final Storage storage;

    public UserRepository(@Nonnull Storage storage) {
        this.storage = storage;

    }

    @Nonnull
    public User save(@Nonnull User user) {
        storage.put(Type.user, conf.asByteArray(user.id), conf.asByteArray(user));
        return user;
    }

    public void save(@Nonnull List<User> users) {
        Map<byte[], byte[]> batch = new HashMap<>(users.size());
        for (User u : users) {
            batch.put(conf.asByteArray(u.id), conf.asByteArray(u));
        }
        storage.bulk(Type.user, batch);
    }

    @Nullable
    public User findById(@Nonnull Integer id) {
        byte[] bytes = storage.get(Type.user, conf.asByteArray(id));
        if (bytes != null) {
            return (User) conf.asObject(bytes);
        }
        return null;
    }

    @Nonnull
    public User update(User user) {
        storage.put(Type.user, conf.asByteArray(user.id), conf.asByteArray(user));
        return user;
    }

    public List<User> findAll() {
        List<byte[]> all = storage.findAll(Type.user);
        List<User> users = new ArrayList<>(all.size());
        for (byte[] bytes : all) {
            users.add((User) conf.asObject(bytes));
        }
        return users;
    }
}