package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.ProjectImportDto;
import com.trails_art.trails.dtos.ProjectExportDto;
import com.trails_art.trails.dtos.export.ExportDtoMethods;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.exceptions.InvalidDTOFormat;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.services.project.ProjectService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<List<ProjectExportDto>> findAll() {
        return new ResponseEntity<>(projectService.findAll().stream().map(ExportDtoMethods::exportProject).toList(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<ProjectExportDto> findById(@PathVariable UUID id) {
        Optional<ProjectExportDto> project;
        try {
            project = projectService.findById(id).map(ExportDtoMethods::exportProject);
        } catch (InvalidArgumentIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        if (project.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found.");
        }
        return new ResponseEntity<>(project.get(), HttpStatus.OK);
    }

    @Transactional
    @PostMapping
    ResponseEntity<Void> create(@Valid @RequestBody ProjectImportDto projectImportDto) {
        try {
            projectService.createFromDto(projectImportDto);
        } catch (InvalidDTOFormat e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Transactional
    @PutMapping("/{id}")
    ResponseEntity<Void> update(@Valid @RequestBody ProjectImportDto projectImportDto, @PathVariable UUID id) {
        try {
            projectService.updateFromDto(projectImportDto, id);
        } catch (InvalidArgumentIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InvalidDTOFormat e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Transactional
    @PostMapping("/{id}/artists")
    ResponseEntity<Void> addNonExistingArtist(@Valid @RequestBody ProjectImportDto.ArtistData artistDto, @PathVariable UUID id) {
        Image image;
        try {
           image = new Image(artistDto.image().mimetype(), Base64.getDecoder().decode(artistDto.image().data()));
        } catch (InvalidDTOFormat e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        Artist artist;
        try {
            artist = new Artist(artistDto.name(), image, artistDto.description(), artistDto.instagram_url());
        } catch (InvalidDTOFormat e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        try {
            projectService.addNonExistingArtist(id, artist);
        } catch (InvalidArgumentIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Transactional
    @PutMapping("/{project_id}/artists/{artist_id}")
    ResponseEntity<Void> addExistingArtist(@PathVariable UUID project_id, @PathVariable UUID artist_id) {
        try {
            projectService.addExistingArtist(project_id, artist_id);
        } catch (InvalidArgumentIdException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Transactional
    @DeleteMapping("/{project_id}/artists/{artist_id}")
    ResponseEntity<Void> deleteArtist(@PathVariable UUID project_id, @PathVariable UUID artist_id) {
        try {
            projectService.deleteArtist(project_id, artist_id);
        } catch (InvalidArgumentIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Transactional
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            projectService.delete(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/count")
    ResponseEntity<Integer> count() {
        return new ResponseEntity<>(projectService.count(), HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    ResponseEntity<List<ProjectExportDto>> findByName(@PathVariable String name) {
        List<ProjectExportDto> projects;
        try {
            projects = projectService.findByName(name).stream().map(ExportDtoMethods::exportProject).toList();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(projects, HttpStatus.OK);
    }
}
