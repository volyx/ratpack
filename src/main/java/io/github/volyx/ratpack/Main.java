package io.github.volyx.ratpack;


import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.guice.Guice;
import ratpack.jackson.Jackson;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {

        Config conf = ConfigFactory.load();

		RatpackServer.start(server -> server
			.serverConfig(c -> c.baseDir(BaseDir.find()))
			.registry(Guice.registry(b -> {
                Storage storage = new Storage(conf.getString("rocksdb"));
                storage.put(Type.user, "user".getBytes(), "value".getBytes());
                byte[] res = storage.get(Type.user, "user".getBytes());
                System.out.println(new String(res, "UTF-8"));
                b.bindInstance(storage);
				b.bindInstance(new UserRepository());
				b.bindInstance(new LocationRepository());
                b.bindInstance(new VisitRepository());
			}))
			.handlers(chain -> chain
				.prefix("user", chain1 -> {
					chain1
							.get(":id", ctx -> {
								UserRepository userRepository = ctx.get(UserRepository.class);
								Map<String, String> pathTokens = ctx.getPathTokens();
								String id = pathTokens.get("id");
								ctx.byContent(byContentSpec -> byContentSpec
										.json(() -> {
											ctx.render(Jackson.json(userRepository.findById(id)));
										})
								);
							})
							.post(":id", ctx -> {
								final String id = ctx.getPathTokens().get("id");
								ctx.getRequest().getBody()
										.then(data -> {
											ctx.parse(Jackson.fromJson(User.class)).then(user -> {
												UserRepository userRepository = ctx.get(UserRepository.class);
												log.info("Start updating new user: {}.", id);

												Blocking.get(() -> {
													log.info("Update user to repo: {}.", id);
													return userRepository.update(user);
												}).then(r -> {
													log.info("User update with id: {}.", r);
												});

												log.info("Finish updating new user: {}.", user.id);
											});
										});
							})
							.post(":id/new", ctx -> {
								final String id = ctx.getPathTokens().get("id");
								ctx.getRequest().getBody()
										.then(data -> {
											ctx.parse(Jackson.fromJson(User.class)).then(user -> {
												UserRepository userRepository = ctx.get(UserRepository.class);
												log.info("Start saving new user: {}.", id);

												Blocking.get(() -> {
													log.info("Save user to repo: {}.", id);
													return userRepository.save(user);
												}).then(r -> {
													log.info("User saved with id: {}.", r);
												});

												log.info("Finish saving new user: {}.", user.id);
											});
										});
							});
				})
				.prefix("location", chain1 -> {
					chain1
							.get(":id", ctx -> {
								LocationRepository locationRepository = ctx.get(LocationRepository.class);
								String id = ctx.getPathTokens().get("id");
								ctx.byContent(byContentSpec -> byContentSpec
										.json(() -> {
											ctx.render(Jackson.json(locationRepository.findById(id)));
										})
								);
							})
							.post(":id", ctx -> {
								final String id = ctx.getPathTokens().get("id");
								ctx.getRequest().getBody()
										.then(data -> {
											ctx.parse(Jackson.fromJson(Location.class)).then(location -> {
												LocationRepository locationRepository = ctx.get(LocationRepository.class);
												log.info("Start updating location user: {}.", id);

												Blocking.get(() -> {
													log.info("Update location to repo: {}.", id);
													return locationRepository.update(location);
												}).then(r -> {
													log.info("Location update with id: {}.", r);
												});

												log.info("Finish updating new location: {}.", location.id);
											});
										});
							})
							.post(":id/new", ctx -> {
								final String id = ctx.getPathTokens().get("id");
								ctx.getRequest().getBody()
										.then(data -> {
											ctx.parse(Jackson.fromJson(Location.class)).then(location -> {
												LocationRepository locationRepository = ctx.get(LocationRepository.class);
												log.info("Start saving new location: {}.", id);

												Blocking.get(() -> {
													log.info("Save location to repo: {}.", id);
													return locationRepository.save(location);
												}).then(r -> {
													log.info("Location saved with id: {}.", r);
												});

												log.info("Finish saving new location: {}.", location.id);
											});
										});
							});
				})
				.prefix("visit", chain1 -> {
					chain1
							.get(":id", ctx -> {
								VisitRepository visitRepository = ctx.get(VisitRepository.class);
								String id = ctx.getPathTokens().get("id");
								ctx.byContent(byContentSpec -> byContentSpec
										.json(() -> {
											ctx.render(Jackson.json(visitRepository.findById(id)));
										})
								);
							})
							.post(":id", ctx -> {
								final String id = ctx.getPathTokens().get("id");
								ctx.getRequest().getBody()
										.then(data -> {
											ctx.parse(Jackson.fromJson(Visit.class)).then(visit -> {
												VisitRepository visitRepository = ctx.get(VisitRepository.class);
												log.info("Start updating visit user: {}.", id);

												Blocking.get(() -> {
													log.info("Updating visit to repo: {}.", id);
													return visitRepository.update(visit);
												}).then(r -> {
													log.info("Visit update with id: {}.", r);
												});

												log.info("Finish updating new visit: {}.", visit.id);
											});
										});
							})
							.post(":id/new", ctx -> {
								final String id = ctx.getPathTokens().get("id");
								ctx.getRequest().getBody()
										.then(data -> {
											ctx.parse(Jackson.fromJson(Visit.class)).then(visit -> {
												VisitRepository visitRepository = ctx.get(VisitRepository.class);
												log.info("Start saving new visit: {}.", id);

												Blocking.get(() -> {
													log.info("Save visit to repo: {}.", id);
													return visitRepository.save(visit);
												}).then(r -> {
													log.info("Visit saved with id: {}.", r);
												});

												log.info("Finish saving new visit: {}.", visit.id);
											});
										});
							});
				})
				.get("/users/:id/visits", ctx -> {
//					для получения списка посещений пользователем
				})
				.get("/locations/:id/avg", ctx -> {
//					для получения средней оценки достопримечательности
				})
			)
		);
	}

	static class UserRepository {

		public User save(User user) {
			return null;
		}

		public User findById(String id) {
			return null;
		}

		public User update(User user) {
			return null;
		}
	}

	static class LocationRepository {

		public Location findById(String id) {
            return null;
		}

		public Location update(Location location) {
            return null;
		}

		public Location save(Location location) {
            return null;
		}
	}

	static class VisitRepository {

		public Visit findById(String id) {
            return null;
		}

		public Visit update(Visit visit) {
            return null;
		}

		public Visit save(Visit visit) {
            return null;
		}
	}

	static class Storage {
        private final Map<Type, Integer> typeHandle = new HashMap<>();
        private String path;

        Storage(String path) {
            this.path = path;
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
	}

	enum Type {
	    user, location, visit;
    }


	static class User {
		/**
		 * id - уникальный внешний идентификатор пользователя.
		 * Устанавливается тестирующей системой и используется затем, для проверки ответов сервера.
		 * 32-разрядное целое число.
		 */
		public final Integer id;
		/**
		 * email - адрес электронной почты пользователя.
		 * Тип - unicode-строка длиной до 100 символов.
		 * Гарантируется уникальность.
		 */
		public final String email;
		public final String first_name;
		public final String last_name;
		public final Gender gender;
		public final Long birth_date;


		User(Integer id, String email, String first_name, String last_name, Gender gender, Long birth_date) {
			this.id = id;
			this.email = email;
			this.first_name = first_name;
			this.last_name = last_name;
			this.gender = gender;
			this.birth_date = birth_date;
		}
	}

	enum Gender {
		m,f;
	}

	class Location {
		public final Integer id;
		public final String place;
		public final String country;
		public final String city;
		public final Integer distance;

		Location(Integer id, String place, String country, String city, Integer distance) {
			this.id = id;
			this.place = place;
			this.country = country;
			this.city = city;
			this.distance = distance;
		}
	}

	class Visit {
		public final Integer id;
		public final Integer location;
		public final Integer user;
		public final Long visited_at;
		public final Integer mark;

		Visit(Integer id, Integer location, Integer user, Long visited_at, Integer mark) {
			this.id = id;
			this.location = location;
			this.user = user;
			this.visited_at = visited_at;
			this.mark = mark;
		}
	}
}
