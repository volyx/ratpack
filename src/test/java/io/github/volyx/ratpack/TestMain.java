package io.github.volyx.ratpack;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.model.Visit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TestMain {
    private static final Logger logger = LoggerFactory.getLogger(TestMain.class);
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient client = new OkHttpClient();

    public static String run(String url) throws IOException {
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .url("http://localhost:5050" + url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Test
    public void test() throws IOException {
        Gson gson = new Gson();
        URL url = TestMain.class.getClassLoader().getResource("data.zip");
        List<User> userList = new ArrayList<>();
        List<Location> locationList = new ArrayList<>();
        List<Visit> visitList = new ArrayList<>();
        try (ZipFile file = new ZipFile(Paths.get(url.getPath()).toFile())) {
            Enumeration<? extends ZipEntry> entries = file.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                try (InputStream is = file.getInputStream(entry)) {
                    logger.info("{}", entry.getName());
                    if (entry.getName().contains("users")) {
                        try (JsonReader jsonReader = new JsonReader(new InputStreamReader(is))) {
                            Loader.UserContainer userContainer = gson.fromJson(jsonReader, Loader.UserContainer.class);
                            logger.info("Load {} users", userContainer.users.length);
                            userList.addAll(Arrays.asList(userContainer.users));
                        }
                    }
                    if (entry.getName().contains("location")) {
                        try (JsonReader jsonReader = new JsonReader(new InputStreamReader(is))) {
                            Loader.LocationContainer container = gson.fromJson(jsonReader, Loader.LocationContainer.class);
                            logger.info("Load {} locations", container.locations.length);
                            locationList.addAll(Arrays.asList(container.locations));
                        }
                    }
                    if (entry.getName().contains("visit")) {
                        try (JsonReader jsonReader = new JsonReader(new InputStreamReader(is))) {
                            Loader.VisitContainer container = gson.fromJson(jsonReader, Loader.VisitContainer.class);
                            logger.info("Load {} visits", container.visits.length);
                            visitList.addAll(Arrays.asList(container.visits));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        for (User user : userList) {
            String userJson = run("/user/" + user.id);
            User get = gson.fromJson(userJson, User.class);
            Assert.assertEquals(user.id, get.id);
            Assert.assertEquals(user.email, get.email);
            Assert.assertEquals(user.first_name, get.first_name);
            Assert.assertEquals(user.last_name, get.last_name);
            Assert.assertEquals(user.birth_date, get.birth_date);
            Assert.assertEquals(user.gender, get.gender);
        }
        for (Location location : locationList) {
            String locationJson = run("/location/" + location.id);
            Location get = gson.fromJson(locationJson, Location.class);
            Assert.assertEquals(location.id, get.id);
            Assert.assertEquals(location.distance, get.distance);
            Assert.assertEquals(location.city, get.city);
            Assert.assertEquals(location.country, get.country);
            Assert.assertEquals(location.place, get.place);
        }
        for (Visit visit : visitList) {
            String visitJson = run("/visit/" + visit.id);
            Visit get = gson.fromJson(visitJson, Visit.class);
            Assert.assertEquals(visit.id, get.id);
            Assert.assertEquals(visit.location, get.location);
            Assert.assertEquals(visit.mark, get.mark);
            Assert.assertEquals(visit.user, get.user);
            Assert.assertEquals(visit.visited_at, get.visited_at);
        }

    }
}
