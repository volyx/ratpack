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
import io.github.volyx.ratpack.storage.Storage;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
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

        Storage storage = new Storage(config.getString("rocksdb"));
        int port = config.getInt("port");
        Gson gson = new Gson();
        UserRepository userRepo = new UserRepository(storage);
        LocationRepository locationRepo = new LocationRepository(storage);
        VisitRepository visitRepo = new VisitRepository(storage, locationRepo);

        Loader loader = new Loader(config, userRepo, locationRepo, visitRepo, gson);
        loader.load();
        timestamp = Objects.requireNonNull(loader.getTimestamp());

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

    public static class Exc {
        public String error;
    }
}
