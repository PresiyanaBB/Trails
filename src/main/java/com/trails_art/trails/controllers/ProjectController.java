package com.trails_art.trails.controllers;

import com.trails_art.trails.models.Project;
import com.trails_art.trails.services.project.JdbcProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final JdbcProjectService jdbcProjectService;

    ProjectController(JdbcProjectService jdbcProjectService) {
        this.jdbcProjectService = jdbcProjectService;
    }

    @GetMapping
    List<Project> findAll() {
        return jdbcProjectService.findAll();
    }

    @GetMapping("/{id}")
    Project findById(@PathVariable UUID id) {
        Optional<Project> project = jdbcProjectService.findById(id);
        if(project.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found.");
        }
        return project.get();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody Project project) {
        jdbcProjectService.create(project);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody Project project, @PathVariable UUID id) {
        jdbcProjectService.update(project,id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable UUID id) {
        jdbcProjectService.delete(id);
    }

    @GetMapping("/count")
    int count() { return jdbcProjectService.count(); }

    @GetMapping("/{name}")
    List<Project> findByName(@PathVariable String name) {
        return jdbcProjectService.findByName(name);
    }
}
