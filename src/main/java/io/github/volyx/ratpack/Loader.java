package io.github.volyx.ratpack;

import com.fasterxml.jackson.core.type.TypeReference;
import com.typesafe.config.Config;
import io.github.volyx.ratpack.handler.Json;
import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.repository.LocationRepository;
import io.github.volyx.ratpack.repository.UserRepository;
import io.github.volyx.ratpack.repository.VisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
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

    public Loader(@Nonnull Config config, @Nonnull UserRepository userRepository, @Nonnull LocationRepository locationRepository, @Nonnull VisitRepository visitRepository) {
        this.path = config.getString("load.path");
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.visitRepository = visitRepository;
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
                            UserContainer userContainer = Json.serializer().fromInputStream(is, UserContainer.typeRef());
                            logger.info("Load {} users", userContainer.users.length);
                            userRepository.save(Arrays.asList(userContainer.users));

                        }
                        if (entryName.contains("location")) {
                            LocationContainer container = Json.serializer().fromInputStream(is, LocationContainer.typeRef());
                            logger.info("Load {} locations", container.locations.length);
                            locationRepository.save(Arrays.asList(container.locations));
                        }
                        if (entryName.contains("visit")) {
                            VisitContainer container = Json.serializer().fromInputStream(is, VisitContainer.typeRef());
                            logger.info("Load {} visits", container.visits.length);
                            visitRepository.save(Arrays.asList(container.visits));
                        }
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            Path path = Paths.get(this.path);
            try {
                Files.list(path).forEach(new Consumer<Path>() {
                    @Override
                    public void accept(Path path) {
                        File file = path.toFile();
                        try (InputStream is = Files.newInputStream(path)) {
                            String fileName = file.getName();
                            if (fileName.contains("users")) {
                                UserContainer userContainer = Json.serializer().fromInputStream(is, UserContainer.typeRef());
                                logger.info("Load {} users", userContainer.users.length);
                                userRepository.save(Arrays.asList(userContainer.users));
                            }
                            if (fileName.contains("location")) {
                                LocationContainer container = Json.serializer().fromInputStream(is, LocationContainer.typeRef());
                                logger.info("Load {} locations", container.locations.length);
                                locationRepository.save(Arrays.asList(container.locations));
                            }
                            if (fileName.contains("visit")) {
                                VisitContainer container = Json.serializer().fromInputStream(is, VisitContainer.typeRef());
                                logger.info("Load {} visits", container.visits.length);
                                visitRepository.save(Arrays.asList(container.visits));
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

    public static class UserContainer {
        private UserContainer() {
        }

        public User[] users;
        private static final TypeReference<UserContainer> typeRef = new TypeReference<UserContainer>() {
        };

        public static TypeReference<UserContainer> typeRef() {
            return typeRef;
        }
    }

    public static class LocationContainer {
        private LocationContainer() {
        }

        public Location[] locations;

        private static final TypeReference<LocationContainer> typeRef = new TypeReference<LocationContainer>() {};

        public static TypeReference<LocationContainer> typeRef() {
            return typeRef;
        }
    }

    public static class VisitContainer {
        private VisitContainer() {
        }

        public Visit[] visits;
        private static final TypeReference<VisitContainer> typeRef = new TypeReference<VisitContainer>() {
        };

        public static TypeReference<VisitContainer> typeRef() {
            return typeRef;
        }
    }
}
