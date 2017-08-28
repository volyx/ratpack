package io.github.volyx.ratpack;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.jsoniter.DecodingMode;
import com.jsoniter.JsonIterator;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.github.volyx.ratpack.handler.ErrorHandler;
import io.github.volyx.ratpack.handler.GetHttpHandler;
import io.github.volyx.ratpack.handler.GetPostHttpHandler;
import io.github.volyx.ratpack.handler.LocationHandler;
import io.github.volyx.ratpack.handler.PostHttpHandler;
import io.github.volyx.ratpack.handler.UserHandler;
import io.github.volyx.ratpack.handler.VisitHandler;
import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.repository.Repository;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.Undertow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.Options;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static Long timestamp;
    public static MetricRegistry metricRegistry = new MetricRegistry();

    public static void main(String[] args) {
        String profile = System.getProperty("profile");

        Config config = ConfigFactory.load(String.format("application%s.conf", (profile != null) ? "." + profile : ""));
        log.info("Profile {}", profile);

        int port = config.getInt("port");
        JsonIterator.setMode(DecodingMode.STATIC_MODE);
        JsonIterator.enableStreamingSupport();
        JsonIterator.enableAnnotationSupport();

        try (JsonIterator iterator = JsonIterator.parse("[]")) {
            iterator.read(User[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (JsonIterator iterator = JsonIterator.parse("[]")) {
            iterator.read(Visit[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (JsonIterator iterator = JsonIterator.parse("[]")) {
            iterator.read(Location[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Repository repo = new Repository();
        UserHandler userHandler = new UserHandler(repo);
        VisitHandler visitHandler = new VisitHandler(repo);
        LocationHandler locationHandler = new LocationHandler(repo);
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
            log.info("Timestamp {} is {}", timestamp, LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        final ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(3, TimeUnit.MINUTES);

        Loader loader = new Loader(config, repo);
        loader.load();

        Undertow server = Undertow.builder()
//                .setServerOption(Options.KEEP_ALIVE, true)
                .setIoThreads(1)
                .setWorkerThreads(1)
                .setBufferSize(1024)
                .setDirectBuffers(true)
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
                        .add("/tests/{id}", errorHandler(new GetHttpHandler(locationHandler::test)))
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
