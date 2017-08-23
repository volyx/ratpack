package io.github.volyx.ratpack;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.typesafe.config.Config;
import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.repository.LocationRepository;
import io.github.volyx.ratpack.repository.UserRepository;
import io.github.volyx.ratpack.repository.VisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.channels.MulticastChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Loader {
    private static final Logger logger = LoggerFactory.getLogger(Loader.class);
    @Nonnull
    private final String path;
    @Nonnull
    private final UserRepository userRepository;
    @Nonnull
    private final LocationRepository locationRepository;
    @Nonnull
    private final VisitRepository visitRepository;
    @Nonnull
    private final Gson gson;
    @Nonnull

    public Loader(@Nonnull Config config, @Nonnull UserRepository userRepository, @Nonnull LocationRepository locationRepository, @Nonnull VisitRepository visitRepository, @Nonnull Gson gson) {
        this.path = config.getString("load.path");
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.visitRepository = visitRepository;
        this.gson = gson;
    }

    void load() {
        logger.info("Begin load {}", path);
        if (path.endsWith(".zip")) {
            try (ZipFile file = new ZipFile(Paths.get(path).toFile())) {
                Enumeration<? extends ZipEntry> entries = file.entries();

                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    try (InputStream is = file.getInputStream(entry)) {
                        String entryName = entry.getName();

                        if (entryName.contains("users")) {
                            try (JsonReader jsonReader = new JsonReader(new InputStreamReader(is))) {
                                UserContainer userContainer = gson.fromJson(jsonReader, UserContainer.class);
                                logger.info("Load {} users", userContainer.users.length);
                                userRepository.save(Arrays.asList(userContainer.users));
                            }
                        }
                        if (entryName.contains("location")) {
                            try (JsonReader jsonReader = new JsonReader(new InputStreamReader(is))) {
                                LocationContainer container = gson.fromJson(jsonReader, LocationContainer.class);
                                logger.info("Load {} locations", container.locations.length);
                                locationRepository.save(Arrays.asList(container.locations));
                            }
                        }
                        if (entryName.contains("visit")) {
                            try (JsonReader jsonReader = new JsonReader(new InputStreamReader(is))) {
                                VisitContainer container = gson.fromJson(jsonReader, VisitContainer.class);
                                logger.info("Load {} visits", container.visits.length);
                                visitRepository.save(Arrays.asList(container.visits));
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            Path path = Paths.get(this.path);
            AtomicLong timestamp = new AtomicLong();
            try {
                Files.list(path).forEach(new Consumer<Path>() {
                    @Override
                    public void accept(Path path) {
                            File file = path.toFile();
                            try (InputStream is = Files.newInputStream(path)) {
                                String fileName = file.getName();
                                if (fileName.contains("users")) {
                                    try (JsonReader jsonReader = new JsonReader(new InputStreamReader(is))) {
                                        UserContainer userContainer = gson.fromJson(jsonReader, UserContainer.class);
                                        logger.info("Load {} users", userContainer.users.length);
                                        userRepository.save(Arrays.asList(userContainer.users));
                                    }
                                }
                                if (fileName.contains("location")) {
                                    try (JsonReader jsonReader = new JsonReader(new InputStreamReader(is))) {
                                        LocationContainer container = gson.fromJson(jsonReader, LocationContainer.class);
                                        logger.info("Load {} locations", container.locations.length);
                                        locationRepository.save(Arrays.asList(container.locations));
                                    }
                                }
                                if (fileName.contains("visit")) {
                                    try (JsonReader jsonReader = new JsonReader(new InputStreamReader(is))) {
                                        VisitContainer container = gson.fromJson(jsonReader, VisitContainer.class);
                                        logger.info("Load {} visits", container.visits.length);
                                        visitRepository.save(Arrays.asList(container.visits));
                                    }
                                }
                            } catch (IOException e) {
                                throw new RuntimeException();
                            }

                    }
                });
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }
    public static class UserContainer{
        private UserContainer(){}
        public User[] users;
    }

    public static class LocationContainer{
        private LocationContainer(){}
        public Location[] locations;
    }
    public static class VisitContainer{
        private VisitContainer(){}
        public Visit[] visits;
    }
}
