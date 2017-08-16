package io.github.volyx.ratpack.storage;


import io.github.volyx.ratpack.Main;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.WriteBatch;
import org.rocksdb.WriteOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Execution;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;

public class Storage {
    private static final Logger logger = LoggerFactory.getLogger(Storage.class);
    private final Map<Type, Integer> typeHandle = new HashMap<>();
    private String path;

    public Storage(String path) {
        this.path = path;
        clean(path);
        RocksDB.loadLibrary();
        try (final ColumnFamilyOptions cfOpts = new ColumnFamilyOptions().optimizeUniversalStyleCompaction()) {

            // list of column family descriptors, first entry must always be default column family
            final List<ColumnFamilyDescriptor> cfDescriptors = Arrays.asList(
                    new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts),
                    new ColumnFamilyDescriptor(Type.user.name().getBytes(), cfOpts),
                    new ColumnFamilyDescriptor(Type.location.name().getBytes(), cfOpts),
                    new ColumnFamilyDescriptor(Type.visit.name().getBytes(), cfOpts)
            );

            // a list which will hold the handles for the column families once the db is opened
            final List<ColumnFamilyHandle> columnFamilyHandleList = new ArrayList<>();

            try (final DBOptions options = new DBOptions()
                    .setCreateIfMissing(true)
                    .setCreateMissingColumnFamilies(true);
                 final RocksDB db = RocksDB.open(options, path, cfDescriptors, columnFamilyHandleList)) {
                typeHandle.put(Type.user, 1);
                typeHandle.put(Type.location, 2);
                typeHandle.put(Type.visit, 3);


                try {

                    // do something

                } finally {

                    // NOTE frees the column family handles before freeing the db
                    for (final ColumnFamilyHandle columnFamilyHandle : columnFamilyHandleList) {
                        columnFamilyHandle.close();
                    }
                }
            } catch (RocksDBException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void clean(@Nonnull String path) {
        try {
            Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                    logger.info(file.toString());
                    Files.delete(file);
                    return CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(final Path file, final IOException e) {
                    return handleException(e);
                }

                private FileVisitResult handleException(final IOException e) {
                    e.printStackTrace(); // replace with more robust error handling
                    return TERMINATE;
                }

                @Override
                public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
                        throws IOException {
                    if (e != null) return handleException(e);
                    Files.delete(dir);
                    logger.info(dir.toString());
                    return CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    public byte[] get(Type type, byte[] key) {
        try (final ColumnFamilyOptions cfOpts = new ColumnFamilyOptions().optimizeUniversalStyleCompaction()) {
            final List<ColumnFamilyDescriptor> cfDescriptors = Arrays.asList(
                    new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts),
                    new ColumnFamilyDescriptor(Type.user.name().getBytes(), cfOpts),
                    new ColumnFamilyDescriptor(Type.location.name().getBytes(), cfOpts),
                    new ColumnFamilyDescriptor(Type.visit.name().getBytes(), cfOpts)
            );

            final List<ColumnFamilyHandle> columnFamilyHandleList = new ArrayList<>();

            try (final DBOptions options = new DBOptions();
                 final RocksDB db = RocksDB.open(options, path, cfDescriptors, columnFamilyHandleList)) {
                try {
                    return db.get(columnFamilyHandleList.get(typeHandle.get(type)), key);
                } finally {

                    // NOTE frees the column family handles before freeing the db
                    for (final ColumnFamilyHandle columnFamilyHandle : columnFamilyHandleList) {
                        columnFamilyHandle.close();
                    }
                }
            } catch (RocksDBException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void bulk(Type type, Map<byte[], byte[]> batch) {
        try (final ColumnFamilyOptions cfOpts = new ColumnFamilyOptions().optimizeUniversalStyleCompaction()) {
            final List<ColumnFamilyDescriptor> cfDescriptors = Arrays.asList(
                    new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts),
                    new ColumnFamilyDescriptor(Type.user.name().getBytes(), cfOpts),
                    new ColumnFamilyDescriptor(Type.location.name().getBytes(), cfOpts),
                    new ColumnFamilyDescriptor(Type.visit.name().getBytes(), cfOpts)
            );

            final List<ColumnFamilyHandle> columnFamilyHandleList = new ArrayList<>();

            try (final DBOptions options = new DBOptions();
                 final RocksDB db = RocksDB.open(options, path, cfDescriptors, columnFamilyHandleList)) {
                try {
                    WriteBatch updates = new WriteBatch();
                    for (byte[] key : batch.keySet()) {
                        updates.put(columnFamilyHandleList.get(typeHandle.get(type)), key, batch.get(key));
                    }
                    WriteOptions writeOpts = new WriteOptions();
                    db.write(writeOpts, updates);
                } finally {

                    // NOTE frees the column family handles before freeing the db
                    for (final ColumnFamilyHandle columnFamilyHandle : columnFamilyHandleList) {
                        columnFamilyHandle.close();
                    }
                }
            } catch (RocksDBException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void put(Type type, byte[] key, byte[] value) {
        try (final ColumnFamilyOptions cfOpts = new ColumnFamilyOptions().optimizeUniversalStyleCompaction()) {

            // list of column family descriptors, first entry must always be default column family
            final List<ColumnFamilyDescriptor> cfDescriptors = Arrays.asList(
                    new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts),
                    new ColumnFamilyDescriptor(Type.user.name().getBytes(), cfOpts),
                    new ColumnFamilyDescriptor(Type.location.name().getBytes(), cfOpts),
                    new ColumnFamilyDescriptor(Type.visit.name().getBytes(), cfOpts)
            );

            // a list which will hold the handles for the column families once the db is opened
            final List<ColumnFamilyHandle> columnFamilyHandleList = new ArrayList<>();

            try (final DBOptions options = new DBOptions();
                 final RocksDB db = RocksDB.open(options, path, cfDescriptors, columnFamilyHandleList)) {

                try {
                    db.put(columnFamilyHandleList.get(typeHandle.get(type)), key, value);
                } finally {

                    // NOTE frees the column family handles before freeing the db
                    for (final ColumnFamilyHandle columnFamilyHandle : columnFamilyHandleList) {
                        columnFamilyHandle.close();
                    }
                }
            } catch (RocksDBException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<byte[]> findAll(@Nonnull Type type) {
        try (final ColumnFamilyOptions cfOpts = new ColumnFamilyOptions().optimizeUniversalStyleCompaction()) {

            // list of column family descriptors, first entry must always be default column family
            final List<ColumnFamilyDescriptor> cfDescriptors = Arrays.asList(
                    new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts),
                    new ColumnFamilyDescriptor(Type.user.name().getBytes(), cfOpts),
                    new ColumnFamilyDescriptor(Type.location.name().getBytes(), cfOpts),
                    new ColumnFamilyDescriptor(Type.visit.name().getBytes(), cfOpts)
            );

            // a list which will hold the handles for the column families once the db is opened
            final List<ColumnFamilyHandle> columnFamilyHandleList = new ArrayList<>();

            try (final DBOptions options = new DBOptions();
                 final RocksDB db = RocksDB.open(options, path, cfDescriptors, columnFamilyHandleList)) {
                 List<byte[]> list = new ArrayList<>();
                 try (final RocksIterator iterator = db.newIterator(columnFamilyHandleList.get(typeHandle.get(type)))) {
                    for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                        iterator.status();
                        assert (iterator.key() != null);
                        assert (iterator.value() != null);
                        list.add(iterator.value());
                    }
                 } finally {

                     // NOTE frees the column family handles before freeing the db
                     for (final ColumnFamilyHandle columnFamilyHandle : columnFamilyHandleList) {
                         columnFamilyHandle.close();
                     }
                 }
                return list;

            } catch (RocksDBException e) {
                throw new RuntimeException(e);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
