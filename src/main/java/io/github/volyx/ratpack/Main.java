package io.github.volyx.ratpack;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.github.volyx.ratpack.handler.ErrorHandler;
import io.github.volyx.ratpack.handler.GetHttpHandler;
import io.github.volyx.ratpack.handler.GetPostHttpHandler;
import io.github.volyx.ratpack.handler.LocationHandler;
import io.github.volyx.ratpack.handler.PostHttpHandler;
import io.github.volyx.ratpack.handler.UserHandler;
import io.github.volyx.ratpack.handler.VisitHandler;
import io.github.volyx.ratpack.repository.LocationRepository;
import io.github.volyx.ratpack.repository.UserRepository;
import io.github.volyx.ratpack.repository.VisitRepository;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static Long timestamp;

    public static void main(String[] args) {
        String profile = System.getProperty("profile");

        Config config = ConfigFactory.load(String.format("application%s.conf", (profile != null) ? "." + profile : ""));
        log.info("Profile {}", profile);

        int port = config.getInt("port");
        UserRepository userRepo = new UserRepository();
        LocationRepository locationRepo = new LocationRepository();
        VisitRepository visitRepo = new VisitRepository(locationRepo);
        UserHandler userHandler = new UserHandler(userRepo, visitRepo);
        VisitHandler visitHandler = new VisitHandler(visitRepo);
        LocationHandler locationHandler = new LocationHandler(locationRepo, visitRepo, userRepo);
        log.info("Load options.txt");

        try (InputStream is = Files.newInputStream(Paths.get(config.getString("options")));) {
            Long result;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
                String timestamp1 = br.readLine();
                try {
                    result = Long.parseLong(timestamp1);
                } catch (NumberFormatException e) {
                    throw new RuntimeException();
                }
            }
            timestamp = Objects.requireNonNull(result);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        Loader loader = new Loader(config, userRepo, locationRepo, visitRepo);
        loader.load();

        Undertow server = Undertow.builder()
                .addHttpListener(port, "0.0.0.0")
                .setHandler(Handlers
                        .pathTemplate()
                        .add("/users/{id}", errorHandler(new GetPostHttpHandler(userHandler::get, userHandler::update)))
                        .add("/users/new", errorHandler( new PostHttpHandler(userHandler::create)))
                        .add("/users/{id}/visits", errorHandler( new GetHttpHandler(userHandler::getVisits)))

                        .add("/visits/{id}", errorHandler(new GetPostHttpHandler(visitHandler::get, visitHandler::update)))
                        .add("/visits/new", errorHandler(new PostHttpHandler(visitHandler::create)))

                        .add("/locations/{id}", errorHandler(new GetPostHttpHandler(locationHandler::get, locationHandler::update)))
                        .add("/locations/new", errorHandler(new PostHttpHandler(locationHandler::create)))
                        .add("/locations/{id}/avg", errorHandler(new GetHttpHandler(locationHandler::avg)))
                )
                .build();
        server.start();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
           throw new RuntimeException();
        }
    }

    public static HttpHandler errorHandler(HttpHandler handler) {
        return new ErrorHandler(handler);
    }
}
