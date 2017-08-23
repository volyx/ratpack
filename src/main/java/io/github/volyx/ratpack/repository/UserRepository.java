package io.github.volyx.ratpack.repository;


import io.github.volyx.ratpack.model.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    private Map<Integer, User> users = new ConcurrentHashMap<>(10000);
    public void save(@Nonnull User user) {
        users.put(user.id, user);
    }

    public void save(@Nonnull List<User> users) {
        for (User user : users) {
            save(user);
        }
    }

    @Nullable
    public User findById(@Nonnull Integer id) {
        return users.get(id);
    }
}