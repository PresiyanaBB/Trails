package com.trails_art.trails.services.project;

import com.trails_art.trails.dtos.ProjectImportDto;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectService {

    List<Project> findAll();

    Optional<Project> findById(UUID id);

    List<Project> findAllByIdIn(List<UUID> ids);

    List<Project> findAllWithEmptyArtists();

    void create(Project project);

    void createFromDto(ProjectImportDto projectImportDto);

    void update(Project project, UUID id);

    void updateFromDto(ProjectImportDto projectImportDto, UUID id);

    void delete(UUID id);

    int count();

    void saveAll(List<Project> projects);

    List<Project> findByName(String name);

    void addNonExistingArtist(UUID project_id,Artist artist);

    void addExistingArtist(UUID project_id, UUID artist_id);

    void deleteArtist(UUID project_id, UUID artist_id);
}
