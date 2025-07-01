package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.EventDto;
import com.trails_art.trails.dtos.export.ExportDtoMethods;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.exceptions.InvalidDTOFormat;
import com.trails_art.trails.models.Event;
import com.trails_art.trails.services.event.EventService;
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
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    ResponseEntity<List<EventDto>> findAll() {
        return new ResponseEntity<>(eventService.findAll().stream().map(ExportDtoMethods::eventExport).toList()
                , HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<EventDto> findById(@PathVariable UUID id) {
        Optional<Event> event;
        try {
            event = eventService.findById(id);
        } catch (InvalidArgumentIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        if (event.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found.");
        }
        return new ResponseEntity<>(ExportDtoMethods.eventExport(event.get()), HttpStatus.OK);
    }

    @Transactional
    @PostMapping
    ResponseEntity<Void> create(@Valid @RequestBody EventDto eventDto) {
        try {
            eventService.createFromDto(eventDto);
        } catch (InvalidDTOFormat e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Transactional
    @PutMapping("/{id}")
    ResponseEntity<Void> update(@Valid @RequestBody Event event, @PathVariable UUID id) {
        try {
            eventService.update(event, id);
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
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            eventService.delete(UUID.fromString(id));
        } catch (InvalidArgumentIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/count")
    ResponseEntity<Integer> count() {
        return new ResponseEntity<>(eventService.count(), HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    ResponseEntity<List<EventDto>> findByName(@PathVariable String name) {
        List<EventDto> events;
        try {
            events = eventService.findByName(name).stream().map(ExportDtoMethods::eventExport).toList();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(events, HttpStatus.OK);
    }
}
