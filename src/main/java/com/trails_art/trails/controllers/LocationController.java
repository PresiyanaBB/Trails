package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.dtos.export.ExportDtoMethods;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.exceptions.InvalidDTOFormat;
import com.trails_art.trails.services.location.LocationService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<List<LocationDto>> findAll() {
        return new ResponseEntity<>(locationService.findAll().stream().map(ExportDtoMethods::exportLocation).toList()
                , HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<LocationDto> findById(@PathVariable UUID id) {
        Optional<LocationDto> location;
        try {
            location = locationService.findById(id).map(ExportDtoMethods::exportLocation);
        } catch (InvalidArgumentIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        if(location.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found.");
        }
        return new ResponseEntity<>(location.get(), HttpStatus.OK);
    }

    @Transactional
    @PostMapping
    ResponseEntity<Void> create(@Valid @RequestBody LocationDto locationDto) {
        try {
            locationService.createFromDto(locationDto);
        } catch (InvalidDTOFormat e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Transactional
    @PutMapping("/{id}")
    ResponseEntity<Void> update(@Valid @RequestBody LocationDto locationDto, @PathVariable UUID id) {
        try {
            locationService.updateFromDto(locationDto, id);
        } catch (InvalidDTOFormat e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Transactional
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            locationService.delete(UUID.fromString(id));
        } catch (InvalidArgumentIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/count")
    ResponseEntity<Integer> count() {
        return new ResponseEntity<>(locationService.count(), HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    ResponseEntity<List<LocationDto>> findByName(@PathVariable String name) {
        List<LocationDto> locations;
        try {
            locations = locationService.findByName(name).stream().map(ExportDtoMethods::exportLocation).toList();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(locations, HttpStatus.OK);
    }
}
