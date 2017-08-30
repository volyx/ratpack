package io.github.volyx.ratpack;

import com.jsoniter.JsonIterator;
import com.typesafe.config.Config;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.repository.Repository;
import io.github.volyx.ratpack.utils.Utils;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Loader {
    private static final Logger logger = LoggerFactory.getLogger(Loader.class);
    @Nonnull
    private final String path;
    @Nonnull
    private final Repository repository;
    @Nonnull
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    public Loader(@Nonnull Config config, @Nonnull Repository repository) {
        this.path = config.getString("load.path");
        this.repository = repository;
    }

    void load() {
        logger.info("Begin load {}", path);
        List<Future<Integer>> futureList = new ArrayList<>();
        if (path.endsWith(".zip")) {
            try {
                ZipFile file = new ZipFile(Paths.get(path).toFile());
                Enumeration<? extends ZipEntry> entries = file.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.contains("users")) {
                        futureList.add(executor.submit(new LoadUsers(entry, file, null)));
                    }
                    if (entryName.contains("location")) {
                        futureList.add(executor.submit(new LoadLocation(entry, file, null)));
                    }
                    if (entryName.contains("visit")) {
                        futureList.add(executor.submit(new LoadVisit(entry, file, null)));
                    }

                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            Path path = Paths.get(this.path);
            try {
                Files.list(path).forEach(path1 -> {
                    File file = path1.toFile();
                    String fileName = file.getName();
                    if (fileName.contains("users")) {
                        futureList.add(executor.submit(new LoadUsers(null, null, file)));
                    }
                    if (fileName.contains("location")) {
                        futureList.add(executor.submit(new LoadLocation(null, null, file)));
                    }
                    if (fileName.contains("visit")) {
                        futureList.add(executor.submit(new LoadVisit(null, null, file)));
                    }

                });
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        int all = 0;
        logger.info("All files {}", futureList.size());
        int done = 0;
        for (Future<Integer> future : futureList) {
            try {
                all = all + future.get();
                done++;
                if (done % 10 == 0) {
                    logger.info("done {}", done);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        logger.info("Load {} records", all);
    }


    private class LoadLocation implements Callable<Integer> {

        private final ZipEntry entry;
        private final ZipFile zipFile;
        private final File file;

        public LoadLocation(ZipEntry entry, ZipFile zipFile, File file) {
            this.entry = entry;
            this.zipFile = zipFile;
            this.file = file;
        }

        @Override
        public Integer call() throws Exception {
            try {
                InputStream is = getInputStream(entry, zipFile, file);
                try (JsonIterator iter = JsonIterator.parse(is, 1024)) {
                    iter.readString();
                    Location[] locations = iter.read(Location[].class);
                    repository.save(locations);
                    return locations.length;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }

    private class LoadVisit implements Callable<Integer> {


        private final ZipEntry entry;
        private final ZipFile zipFile;
        private final File file;

        public LoadVisit(ZipEntry entry, ZipFile zipFile, File file) {
            this.entry = entry;
            this.zipFile = zipFile;
            this.file = file;
        }

        @Override
        public Integer call() throws Exception {
            try {
                InputStream is = getInputStream(entry, zipFile, file);
//                logger.info("load visit");
                try (JsonIterator iter = JsonIterator.parse(is, 1024)) {
//                    logger.info("load " + );
                    iter.readString();
                    Visit[] visits = iter.read(Visit[].class);
                    repository.save(visits);
                    return visits.length;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }

    public class LoadUsers implements Callable<Integer> {

        private ZipEntry entry;
        private ZipFile zipFile;
        private final File file;

        public LoadUsers(ZipEntry entry, ZipFile zipFile, File file) {
            this.entry = entry;
            this.zipFile = zipFile;
            this.file = file;
        }

        @Override
        public Integer call() throws Exception {
            try {
                InputStream is = getInputStream(entry, zipFile, file);
                try (JsonIterator iter = JsonIterator.parse(is, 1024)) {
                    iter.readString();
                    User[] users = iter.read(User[].class);
                    TIntObjectHashMap userMap = new TIntObjectHashMap(users.length);
                    for (User user : users) {
//                        Utils.getAge(user.birth_date);
                        userMap.put(user.id, user);
                    }
                    repository.save(userMap);
                    return users.length;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }

    private static InputStream getInputStream(ZipEntry entry, ZipFile zipFile, File file) throws IOException {
        if (file != null) {
            return Files.newInputStream(file.toPath());
        }
        return zipFile.getInputStream(entry);
    }
}
