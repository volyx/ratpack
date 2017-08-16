package io.github.volyx.ratpack;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.repository.LocationRepository;
import io.github.volyx.ratpack.repository.UserRepository;
import io.github.volyx.ratpack.repository.VisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
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
    private final ObjectMapper mapper;

    public Loader(@Nonnull String path, @Nonnull UserRepository userRepository, @Nonnull LocationRepository locationRepository, @Nonnull VisitRepository visitRepository, @Nonnull ObjectMapper mapper) {
        this.path = path;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.visitRepository = visitRepository;
        this.mapper = mapper;
    }

    void load() {
        logger.info("Begin unzip {}", path);
        try (ZipFile file = new ZipFile(Paths.get(path).toFile())) {
            Enumeration<? extends ZipEntry> entries = file.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                try (BufferedInputStream bis = new BufferedInputStream(file.getInputStream(entry))) {
                    logger.info("{}", entry.getName());
                    if (entry.getName().contains("users")) {
                        UserContainer userContainer = mapper.readValue(bis, UserContainer.class);
                        logger.info("Load {} users", userContainer.users.length);
                        userRepository.save(Arrays.asList(userContainer.users));
                    }
                    if (entry.getName().contains("location")) {
                        LocationContainer container = mapper.readValue(bis, LocationContainer.class);
                        logger.info("Load {} locations", container.locations.length);
                        locationRepository.save(Arrays.asList(container.locations));
                    }
                    if (entry.getName().contains("visit")) {
                        VisitContainer container = mapper.readValue(bis, VisitContainer.class);
                        logger.info("Load {} visits", container.visits.length);
                        visitRepository.save(Arrays.asList(container.visits));
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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
