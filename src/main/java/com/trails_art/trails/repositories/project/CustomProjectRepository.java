package com.trails_art.trails.repositories.project;

import com.trails_art.trails.models.Project;

import java.util.List;

public interface CustomProjectRepository {
    List<Project> findAllWithEmptyArtists();
}
