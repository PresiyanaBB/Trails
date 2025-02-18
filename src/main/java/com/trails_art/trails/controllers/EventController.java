package com.trails_art.trails.controllers;

import com.trails_art.trails.models.Event;
import com.trails_art.trails.services.event.JdbcEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final JdbcEventService jdbcEventService;

    EventController(JdbcEventService jdbcEventService) {
        this.jdbcEventService = jdbcEventService;
    }

    @GetMapping
    List<Event> findAll() {
        return jdbcEventService.findAll();
    }

    @GetMapping("/{id}")
    Event findById(@PathVariable UUID id) {
        Optional<Event> event = jdbcEventService.findById(id);
        if(event.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found.");
        }
        return event.get();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody Event event) {
        jdbcEventService.create(event);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody Event event, @PathVariable UUID id) {
        jdbcEventService.update(event,id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable UUID id) {
        jdbcEventService.delete(id);
    }

    @GetMapping("/count")
    int count() { return jdbcEventService.count(); }

    @GetMapping("/{name}")
    List<Event> findByName(@PathVariable String name) {
        return jdbcEventService.findByName(name);
    }
}
