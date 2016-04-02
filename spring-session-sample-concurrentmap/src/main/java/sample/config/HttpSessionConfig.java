package sample.config;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.ExpiringSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.web.http.SessionRepositoryFilter;

/** Created by igor.mukhin on 02.10.2015. */
@Configuration
public class HttpSessionConfig {

	private Integer maxInactiveIntervalInSeconds = 1800;
	//private static final Logger logger = LoggerFactory.getLogger(HttpSessionConfig.class);
	private static final Logger logger = LoggerFactory.getLogger(HttpSessionConfig.class);

	@Value("${spring.application.name}")
	private String appname;

	// It does not persist session over server restarts
	@Bean
	@Profile("!persistent")
	//@Profile("persistent")
	public MapSessionRepository mapSessionRepository() {
		logger.info("------------1");
		MapSessionRepository sessionRepository = new MapSessionRepository();
		sessionRepository.setDefaultMaxInactiveInterval(maxInactiveIntervalInSeconds);
		return sessionRepository;
	}

	// This one persists sessions over server restarts if you are running in a standalone Tomcat
	@Bean
	@Profile("persistent")
	//@Profile("!persistent")
	public PersistentMapSessionRepository persistentMapSessionRepository() {
		// Will not work in IntelliJ IDEA
		//File tmpdir = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
		//File sessionsFile = new File(tmpdir, "sessions.dat");
		//System.out.println("***************** " + sessionsFile);
		logger.info("------------2");
		File sessionsFile = new File(appname + "-sessions.dat");
		System.out.println("***************** " + sessionsFile);

		PersistentMapSessionRepository sessionRepository = new PersistentMapSessionRepository(sessionsFile, new ConcurrentHashMap<>());
		sessionRepository.setDefaultMaxInactiveInterval(maxInactiveIntervalInSeconds);

		return sessionRepository;
	}

	@Bean
	public <S extends ExpiringSession> SessionRepositoryFilter<? extends ExpiringSession> springSessionRepositoryFilter(SessionRepository<S> sessionRepository,
			ServletContext servletContext) {
		SessionRepositoryFilter<S> sessionRepositoryFilter = new SessionRepositoryFilter<S>(sessionRepository);
		sessionRepositoryFilter.setServletContext(servletContext);
		return sessionRepositoryFilter;
	}

}
