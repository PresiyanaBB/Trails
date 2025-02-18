package com.trails_art.trails.services.project;

import com.trails_art.trails.models.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectService {

    List<Project> findAll();

    Optional<Project> findById(UUID id);

    void create(Project project);

    void update(Project project, UUID id);

    void delete(UUID id);

    int count();

    void saveAll(List<Project> projects);

    List<Project> findByName(String name);
}
