package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.ArtistDataDto;
import com.trails_art.trails.dtos.ArtistExportDto;
import com.trails_art.trails.dtos.ProjectImportDto;
import com.trails_art.trails.dtos.ProjectExportDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.mappers.ArtistMapper;
import com.trails_art.trails.mappers.ProjectMapper;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Project;
import com.trails_art.trails.services.project.ProjectService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectExportDto>> findAll() {
        List<ProjectExportDto> dtos = projectService.findAll().stream()
                .map(ProjectMapper::mapToProjectDto)
                .toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(dtos.size()))
                .body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectExportDto> findById(@PathVariable UUID id) {
        Project project = projectService.findById(id)
                .orElseThrow(() -> new InvalidArgumentIdException("Project not found."));
        return ResponseEntity.ok(ProjectMapper.mapToProjectDto(project));
    }

    @GetMapping(params = "name")
    public ResponseEntity<List<ProjectExportDto>> findByName(@RequestParam String name) {
        List<ProjectExportDto> dtos = projectService.findByName(name).stream()
                .map(ProjectMapper::mapToProjectDto)
                .toList();

        if (dtos.isEmpty()) {
            throw new InvalidArgumentIdException("No projects found with name: " + name);
        }
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ProjectExportDto> create(@Valid @RequestBody ProjectImportDto dto) {
        Project project = projectService.createFromDto(dto);
        URI location = URI.create("/api/projects/" + project.getId());
        return ResponseEntity.created(location)
                .body(ProjectMapper.mapToProjectDto(project));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ProjectExportDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProjectImportDto dto
    ) {
        Project project = projectService.updateFromDto(dto, id);
        return ResponseEntity.ok(ProjectMapper.mapToProjectDto(project));
    }

    @PostMapping("/{projectId}/artists")
    @Transactional
    public ResponseEntity<ArtistExportDto> addNonExistingArtist(
            @PathVariable UUID projectId,
            @Valid @RequestBody ArtistDataDto artistDto
    ) {
        Artist artist = ArtistMapper.mapToArtist(artistDto);
        projectService.addNonExistingArtist(projectId, artist);

        URI location = URI.create("/api/artists/" + artist.getId());
        return ResponseEntity.created(location)
                .body(ArtistMapper.mapToArtistDto(artist));
    }

    @PutMapping("/{projectId}/artists/{artistId}")
    @Transactional
    public ResponseEntity<Void> addExistingArtist(
            @PathVariable UUID projectId,
            @PathVariable UUID artistId
    ) {
        projectService.addExistingArtist(projectId, artistId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{projectId}/artists/{artistId}")
    @Transactional
    public ResponseEntity<Void> deleteArtist(
            @PathVariable UUID projectId,
            @PathVariable UUID artistId
    ) {
        projectService.deleteArtist(projectId, artistId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
