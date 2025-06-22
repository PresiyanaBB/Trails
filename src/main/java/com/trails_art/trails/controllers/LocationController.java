package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.services.location.LocationService;
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

    private final LocationService locationService;

    LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    List<Location> findAll() {
        return locationService.findAll();
    }

    @GetMapping("/{id}")
    Location findById(@PathVariable UUID id) {
        Optional<Location> location = locationService.findById(id);
        if(location.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found.");
        }
        return location.get();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody LocationDto locationDto) {
        locationService.createFromDto(locationDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody LocationDto locationDto, @PathVariable UUID id) {
        locationService.updateFromDto(locationDto, id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable String id) {
        locationService.delete(UUID.fromString(id));
    }

    @GetMapping("/count")
    int count() { return locationService.count(); }

    @GetMapping("/name/{name}")
    List<Location> findByName(@PathVariable String name) {
        return locationService.findByName(name);
    }
}
