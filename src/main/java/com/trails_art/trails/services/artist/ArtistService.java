package com.trails_art.trails.services.artist;

import com.trails_art.trails.dtos.ArtistImportDto;
import com.trails_art.trails.models.Artist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistService {
    List<Artist> findAll();

    Optional<Artist> findById(UUID id);

    void create(Artist artist);

    void createFromDto(ArtistImportDto artistImportDto);

    void update(Artist artist, UUID id);

    void updateFromDto(ArtistImportDto artistImportDto, UUID id);

    void delete(UUID id);

    int count();

    void saveAll(List<Artist> artists);

    List<Artist> findByName(String name);

    void addProjects(List<UUID> projects, UUID artistId);
}
