package io.github.volyx.ratpack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.model.Visit;
import io.github.volyx.ratpack.repository.LocationRepository;
import io.github.volyx.ratpack.repository.UserRepository;
import io.github.volyx.ratpack.repository.VisitRepository;
import io.github.volyx.ratpack.storage.Storage;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.func.Function;
import ratpack.guice.Guice;
import ratpack.jackson.Jackson;
import ratpack.registry.Registry;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Map;
import java.util.Set;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static FSTConfiguration conf = FSTConfiguration.createMinBinConfiguration();

    public static void main(String[] args) throws Exception {
        String profile = System.getProperty("profile");
        Config config = ConfigFactory.load(String.format("application%s.conf", (profile != null)?  "." + profile : ""));

        Storage storage = new Storage(config.getString("rocksdb"));
        UserRepository userRepo = new UserRepository(storage);
        LocationRepository locationRepo = new LocationRepository(storage);
        VisitRepository visitRepo = new VisitRepository(storage);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Function<Registry, Registry> registry = Guice.registry(b -> {
            b.bindInstance(validator);
            b.bindInstance(storage);
            b.bindInstance(userRepo);
            b.bindInstance(locationRepo);
            b.bindInstance(visitRepo);
        });

        ObjectMapper mapper = new ObjectMapper();

        new Loader(config.getString("load.path"), userRepo, locationRepo, visitRepo, mapper).load();

        RatpackServer s = RatpackServer.of(server -> {

                    server
                            .serverConfig(c -> c.baseDir(BaseDir.find()))
                            .registry(registry)
                            .handlers(chain -> chain
                                            .prefix("user", chain1 -> {
                                                chain1
                                                        .get(":id", ctx -> {
                                                            Map<String, String> pathTokens = ctx.getPathTokens();
                                                            String idParam = pathTokens.get("id");
                                                            ctx.byContent(byContentSpec -> byContentSpec
                                                                    .json(() -> {
                                                                        Integer id;
                                                                        try {
                                                                            id = Integer.parseInt(idParam);
                                                                        } catch (NumberFormatException e) {
                                                                            log.error(e.getMessage(), e);
                                                                            ctx.getResponse().status(400).send(e.getMessage());
                                                                            return;
                                                                        }
                                                                        UserRepository userRepository = ctx.get(UserRepository.class);
                                                                        @Nullable User user = userRepository.findById(id);
                                                                        if (user == null) {
                                                                            ctx.getResponse().status(400).send("Not found " + id);
                                                                            return;
                                                                        }
                                                                        ctx.render(Jackson.json(user));
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
                                                                            Set<ConstraintViolation<User>> violations = validator.validate(user);
                                                                            for (ConstraintViolation<User> violation : violations) {
                                                                                log.error(violation.getMessage());
                                                                            }
                                                                            if (!violations.isEmpty()) {
                                                                                ctx.getResponse().status(400).send(String.valueOf(violations.stream().map(ConstraintViolation::getMessage).reduce((s1, s2) -> s1 + s2)));
                                                                                return;
                                                                            }
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
                                                            String idParam = ctx.getPathTokens().get("id");
                                                            ctx.byContent(byContentSpec -> byContentSpec
                                                                    .json(() -> {
                                                                        Integer id;
                                                                        try {
                                                                            id = Integer.parseInt(idParam);
                                                                        } catch (NumberFormatException e) {
                                                                            log.error(e.getMessage(), e);
                                                                            ctx.getResponse().status(400).send(e.getMessage());
                                                                            return;
                                                                        }
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
                                                            String idParam = ctx.getPathTokens().get("id");
                                                            ctx.byContent(byContentSpec -> byContentSpec
                                                                    .json(() -> {
                                                                        Integer id;
                                                                        try {
                                                                            id = Integer.parseInt(idParam);
                                                                        } catch (NumberFormatException e) {
                                                                            log.error(e.getMessage(), e);
                                                                            ctx.getResponse().status(400).send(e.getMessage());
                                                                            return;
                                                                        }
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
                            );
                }
        );

        s.start();

//        ServerBackedApplicationUnderTest.of(s).test(httpClient -> {
//            User user = new User(1, "123", "a", "v", Gender.m, System.currentTimeMillis());
//            Assert.assertTrue(httpClient.post("user/" + user.id + "/new").getStatus().equals(Status.OK));
//            for (User user : userRepo.findAll()) {
//                Assert.assertTrue(httpClient.get("user/" + user.id).getStatus().equals(Status.OK));
//            }
//            for (Location location : locationRepo.findAll()) {
//                Assert.assertTrue(httpClient.get("location/" + location.id).getStatus().equals(Status.OK));
//            }
//            for (Visit visit : visitRepo.findAll()) {
//                Assert.assertTrue(httpClient.get("visit/" + visit.id).getStatus().equals(Status.OK));
//            }
//        });
    }





}
