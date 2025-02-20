package com.trails_art.trails;

import com.trails_art.trails.clients.HttpClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@Import(HttpClientConfig.class)
@SpringBootApplication
public class TrailsApplication {

	private static final Logger log = LoggerFactory.getLogger(TrailsApplication.class);
	public static void main(String[] args) {

		SpringApplication.run(TrailsApplication.class, args);

		log.info("Application started successfully");

	}

}
