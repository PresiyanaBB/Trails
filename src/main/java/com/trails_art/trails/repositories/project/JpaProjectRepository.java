package com.trails_art.trails.repositories.project;

import com.trails_art.trails.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaProjectRepository extends JpaRepository<Project, UUID>, CustomProjectRepository {
    List<Project> findByNameContainingIgnoreCase(String name);

    List<Project> findAllWithEmptyArtists();
}

