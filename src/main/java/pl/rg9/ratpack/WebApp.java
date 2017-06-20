package pl.rg9.ratpack;

import static ratpack.thymeleaf.Template.*;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ratpack.exec.Blocking;
import ratpack.form.Form;
import ratpack.guice.Guice;
import ratpack.jackson.Jackson;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;
import ratpack.session.Session;
import ratpack.session.SessionModule;
import ratpack.thymeleaf.ThymeleafModule;

public class WebApp {

	private static final Logger log = LoggerFactory.getLogger(WebApp.class);

	private static final String USERNAME = "username";

	public static void main(String[] args) throws Exception {
		RatpackServer.start(server -> server
			.serverConfig(c -> c.baseDir(BaseDir.find()))
			.registry(Guice.registry(b -> {
				b.module(ThymeleafModule.class);
				b.module(SessionModule.class);
				b.bindInstance(new UserRepository());
			}))
			.handlers(chain -> chain
				.prefix("user", chain1 -> chain1
					.get("list", ctx -> {
						UserRepository userRepository = ctx.get(UserRepository.class);
						ctx.byContent(byContentSpec -> byContentSpec
							.html(() -> {
								ctx.render(thymeleafTemplate("user/list", m -> m.put("users", userRepository.findAll())));
							})
							.json(() -> {
								ctx.render(Jackson.json(userRepository.findAll()));
							})
						);
					})
					.post("new", ctx -> {
						ctx.parse(Form.class).then(form -> {
							String username = form.get("username");

							UserRepository userRepository = ctx.get(UserRepository.class);

							log.info("Start saving new user: {}.", username);


							Blocking.get(() -> {
								Thread.sleep(3000);
								log.info("Save user to repo: {}.", username);
								return userRepository.save(username);
							}).then(r -> {
								log.info("User saved with id: {}.", r);
							});

							ctx.get(Session.class).getData()
								.then(sessionData -> {
									log.info("Save user to session: {}.", username);
									sessionData.set(USERNAME, username);
									ctx.redirect("/");
								});


							log.info("Finish saving new user: {}.", username);
						});

					}))
				.get(ctx -> {
					ctx.get(Session.class).getData()
						.map(sessionData -> sessionData.get(USERNAME))
						.then(username -> {
							if (username.isPresent()) {
								ctx.render(thymeleafTemplate("welcome", m -> m.put(USERNAME, username.get())));
							} else {
								ctx.render(thymeleafTemplate("who-are-you"));
							}
						});
				})
			)
		);
	}

	static class UserRepository {

		private final AtomicLong ids = new AtomicLong();
		private final Map<Long, User> users = new ConcurrentHashMap<>();

		UserRepository() {
			save("Paulina");
			save("Magda");
			save("Wiktoria");
		}

		Collection<User> findAll() {
			return users.values();
		}

		long save(String userName) {
			long id = ids.incrementAndGet();
			users.put(id, new User(id, userName));
			return id;
		}
	}

	static class User {
		final Long id;
		final String name;

		public User(Long id, String name) {
			this.id = id;
			this.name = name;
		}

		public Long getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}
}
