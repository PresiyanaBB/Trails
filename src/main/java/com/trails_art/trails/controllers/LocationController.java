package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.services.location.JpaLocationService;
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

    private final JpaLocationService jpaLocationService;

    LocationController(JpaLocationService jpaLocationService) {
        this.jpaLocationService = jpaLocationService;
    }

    @GetMapping
    List<Location> findAll() {
        return jpaLocationService.findAll();
    }

    @GetMapping("/{id}")
    Location findById(@PathVariable UUID id) {
        Optional<Location> location = jpaLocationService.findById(id);
        if(location.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found.");
        }
        return location.get();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody LocationDto locationDto) {
        Location location = new Location(locationDto.name(), locationDto.map_address());
        jpaLocationService.create(location);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody LocationDto locationDto, @PathVariable UUID id) {
        Location location = jpaLocationService.findById(id).orElseThrow();
        location.setName(locationDto.name());
        location.setMapAddress(locationDto.map_address());
        jpaLocationService.update(location,id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable String id) {
        jpaLocationService.delete(UUID.fromString(id));
    }

    @GetMapping("/count")
    int count() { return jpaLocationService.count(); }

    @GetMapping("/name/{name}")
    List<Location> findByName(@PathVariable String name) {
        return jpaLocationService.findByName(name);
    }
}
