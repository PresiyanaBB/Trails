package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.ProjectImportDto;
import com.trails_art.trails.dtos.ProjectExportDto;
import com.trails_art.trails.dtos.export.ExportDtoMethods;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Project;
import com.trails_art.trails.services.project.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    List<ProjectExportDto> findAll() {
        return projectService.findAll().stream().map(ExportDtoMethods::exportProject).toList();
    }

    @GetMapping("/{id}")
    ProjectExportDto findById(@PathVariable UUID id) {
        Optional<Project> project = projectService.findById(id);
        if (project.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found.");
        }
        return ExportDtoMethods.exportProject(project.get());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody ProjectImportDto projectImportDto) {
        projectService.createFromDto(projectImportDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody ProjectImportDto projectImportDto, @PathVariable UUID id) {
        projectService.updateFromDto(projectImportDto, id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{id}/artists")
    void addNonExistingArtist(@Valid @RequestBody ProjectImportDto.ArtistData artistDto, @PathVariable UUID id) {
        Image image = new Image(artistDto.image().mimetype(), Base64.getDecoder().decode(artistDto.image().data()));
        Artist artist = new Artist(artistDto.name(), image, artistDto.description(), artistDto.instagram_url());
        projectService.addNonExistingArtist(id, artist);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("/{project_id}/artists/{artist_id}")
    void addExistingArtist(@PathVariable UUID project_id, @PathVariable UUID artist_id) {
        projectService.addExistingArtist(project_id, artist_id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{project_id}/artists/{artist_id}")
    void deleteArtist(@PathVariable UUID project_id, @PathVariable UUID artist_id) {
        projectService.deleteArtist(project_id, artist_id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable String id) {
        projectService.delete(UUID.fromString(id));
    }

    @GetMapping("/count")
    int count() {
        return projectService.count();
    }

    @GetMapping("/name/{name}")
    List<ProjectExportDto> findByName(@PathVariable String name) {
        return projectService.findByName(name).stream().map(ExportDtoMethods::exportProject).toList();
    }
}
