package io.github.volyx.ratpack;

import co.cask.http.ExceptionHandler;
import co.cask.http.HttpResponder;
import co.cask.http.NettyHttpService;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.github.volyx.ratpack.handler.LocationHandler;
import io.github.volyx.ratpack.handler.UserHandler;
import io.github.volyx.ratpack.handler.VisitHandler;
import io.github.volyx.ratpack.repository.LocationRepository;
import io.github.volyx.ratpack.repository.UserRepository;
import io.github.volyx.ratpack.repository.VisitRepository;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
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
    public static FSTConfiguration conf = FSTConfiguration.createMinBinConfiguration();
    @Nonnull
    public static Long timestamp;

    public static void main(String[] args) {
        String profile = System.getProperty("profile");

        Config config = ConfigFactory.load(String.format("application%s.conf", (profile != null) ? "." + profile : ""));
        log.info("Profile {}", profile);

        int port = config.getInt("port");
        Gson gson = new Gson();
        UserRepository userRepo = new UserRepository();
        LocationRepository locationRepo = new LocationRepository();
        VisitRepository visitRepo = new VisitRepository(locationRepo);

        System.out.println("Load options.txt");

        try (InputStream is = Files.newInputStream(Paths.get(config.getString("options")));) {
            timestamp = Objects.requireNonNull(readTimestamp(is));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        Loader loader = new Loader(config, userRepo, locationRepo, visitRepo, gson);
        loader.load();

        NettyHttpService service = NettyHttpService.builder("super")
                .setPort(port)
                .setHost("0.0.0.0")
                .addHttpHandlers(ImmutableList.of(
                        new UserHandler(userRepo, visitRepo, gson),
                        new LocationHandler(locationRepo, visitRepo, userRepo, gson),
                        new VisitHandler(visitRepo, gson)
                ))
                .setExceptionHandler(new ExceptionHandler() {

                    @Override
                    public void handle(Throwable t, HttpRequest request, HttpResponder responder) {
                        Exc exc = new Exc();
                        exc.error = t.getMessage();
                        log.error(t.getMessage(), t);
                        responder.sendJson(HttpResponseStatus.BAD_REQUEST, gson.toJson(exc));
                    }
                })
                .build();

       service.startAndWait();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static Long readTimestamp(@Nonnull InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
            String timestamp = br.readLine();
            try {
                return Long.parseLong(timestamp);
            } catch (NumberFormatException e) {
                throw new RuntimeException();
            }
        }
    }


    public static class Exc {
        public String error;
    }
}
