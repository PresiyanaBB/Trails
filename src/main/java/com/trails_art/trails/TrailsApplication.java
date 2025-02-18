package com.trails_art.trails;

import com.trails_art.trails.clients.ArtistHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
public class TrailsApplication {

	private static final Logger log = LoggerFactory.getLogger(TrailsApplication.class);
	public static void main(String[] args) {

		SpringApplication.run(TrailsApplication.class, args);
		log.info("Application started successfully");
	}

	@Bean
	ArtistHttpClient artistHttpClient() {
		RestClient restClient = RestClient.create("http://localhost:8080/");
		HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
		return factory.createClient(ArtistHttpClient.class);
	}
}
