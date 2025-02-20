package com.trails_art.trails.services.artist_project;

import com.trails_art.trails.models.ArtistProject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistProjectService {
    List<ArtistProject> findAll();

    Optional<ArtistProject> findById(UUID id);

    void create(ArtistProject artistProject);

    void update(ArtistProject artistProject, UUID id);

    void delete(UUID id);

    int count();

    void saveAll(List<ArtistProject> artistProjects);
}

