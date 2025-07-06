package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.mappers.LocationMapper;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.services.location.LocationService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> findAll() {
        List<LocationDto> dtos = locationService.findAll().stream()
                .map(LocationMapper::mapToLocationDto)
                .toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(dtos.size()))
                .body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> findById(@PathVariable UUID id) {
        Location location = locationService.findById(id)
                .orElseThrow(() -> new InvalidArgumentIdException("Location not found."));
        return ResponseEntity.ok(LocationMapper.mapToLocationDto(location));
    }

    @GetMapping(params = "name")
    public ResponseEntity<List<LocationDto>> findByName(@RequestParam String name) {
        List<LocationDto> dtos = locationService.findByName(name).stream()
                .map(LocationMapper::mapToLocationDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<LocationDto> create(@Valid @RequestBody LocationDto dto) {
        Location location = locationService.createFromDto(dto);
        URI uri = URI.create("/api/locations/" + location.getId());
        return ResponseEntity.created(uri)
                .body(LocationMapper.mapToLocationDto(location));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<LocationDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody LocationDto dto
    ) {
        Location location = locationService.updateFromDto(dto, id);
        return ResponseEntity.ok(LocationMapper.mapToLocationDto(location));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
