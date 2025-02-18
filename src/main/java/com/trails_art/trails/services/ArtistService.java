package com.trails_art.trails.services;

import com.trails_art.trails.models.Artist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistService {
    List<Artist> findAll();

    Optional<Artist> findById(UUID id);

    void create(Artist artist);

    void update(Artist artist, UUID id);

    void delete(UUID id);

    int count();

    void saveAll(List<Artist> artists);

    List<Artist> findByName(String name);
}
