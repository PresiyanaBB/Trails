package com.trails_art.trails.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientConfig {
    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    @Bean
    public ArtistHttpClient artistHttpClient(RestClient restClient, @Value("${api.artists.url}") String baseUrl) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient.mutate().baseUrl(baseUrl).build()))
                .build();
        return factory.createClient(ArtistHttpClient.class);
    }

    @Bean
    public EventHttpClient eventHttpClient(RestClient restClient, @Value("${api.events.url}") String baseUrl) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient.mutate().baseUrl(baseUrl).build()))
                .build();
        return factory.createClient(EventHttpClient.class);
    }

    @Bean
    public ImageHttpClient imageHttpClient(RestClient restClient, @Value("${api.images.url}") String baseUrl) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient.mutate().baseUrl(baseUrl).build()))
                .build();
        return factory.createClient(ImageHttpClient.class);
    }

    @Bean
    public LocationHttpClient locationHttpClient(RestClient restClient, @Value("${api.locations.url}") String baseUrl) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient.mutate().baseUrl(baseUrl).build()))
                .build();
        return factory.createClient(LocationHttpClient.class);
    }

    @Bean
    public ProjectHttpClient projectHttpClient(RestClient restClient, @Value("${api.projects.url}") String baseUrl) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient.mutate().baseUrl(baseUrl).build()))
                .build();
        return factory.createClient(ProjectHttpClient.class);
    }
}
