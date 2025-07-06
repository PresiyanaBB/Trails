package com.trails_art.trails.services.artist;

import com.trails_art.trails.dtos.ArtistImportDto;
import com.trails_art.trails.models.Artist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistService {
    List<Artist> findAll();

    Optional<Artist> findById(UUID id);

    List<Artist> findAllWithEmptyProjects();

    void create(Artist artist);

    Artist createFromDto(ArtistImportDto artistImportDto);

    void update(Artist artist, UUID id);

    Artist updateFromDto(ArtistImportDto artistImportDto, UUID id);

    void delete(UUID id);

    int count();

    List<Artist> findByName(String name);

    void addProjects(List<UUID> projects, UUID artistId);
}
