package com.trails_art.trails.controllers;

import com.trails_art.trails.models.Location;
import com.trails_art.trails.services.location.JdbcLocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final JdbcLocationService jdbcLocationService;

    LocationController(JdbcLocationService jdbcLocationService) {
        this.jdbcLocationService = jdbcLocationService;
    }

    @GetMapping
    List<Location> findAll() {
        return jdbcLocationService.findAll();
    }

    @GetMapping("/{id}")
    Location findById(@PathVariable UUID id) {
        Optional<Location> location = jdbcLocationService.findById(id);
        if(location.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found.");
        }
        return location.get();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody Location location) {
        jdbcLocationService.create(location);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody Location location, @PathVariable UUID id) {
        jdbcLocationService.update(location,id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable UUID id) {
        jdbcLocationService.delete(id);
    }

    @GetMapping("/count")
    int count() { return jdbcLocationService.count(); }

    @GetMapping("/{name}")
    List<Location> findByName(@PathVariable String name) {
        return jdbcLocationService.findByName(name);
    }

    @GetMapping("/map/{mapAddress}")
    List<Location> findByMapAddress(@PathVariable String mapAddress){
        return jdbcLocationService.findByMapAddress(mapAddress);
    }
}
