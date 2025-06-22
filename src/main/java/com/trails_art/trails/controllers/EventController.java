package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.EventDto;
import com.trails_art.trails.dtos.export.ExportDtoMethods;
import com.trails_art.trails.models.Event;
import com.trails_art.trails.services.event.EventService;
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

    private final EventService eventService;

    EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    List<EventDto> findAll() {
        return eventService.findAll().stream().map(ExportDtoMethods::eventExport).toList();
    }

    @GetMapping("/{id}")
    EventDto findById(@PathVariable UUID id) {
        Optional<Event> event = eventService.findById(id);
        if(event.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found.");
        }
        return ExportDtoMethods.eventExport(event.get());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody EventDto eventDto) {
        eventService.createFromDto(eventDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody Event event, @PathVariable UUID id) {
        eventService.update(event,id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable String id) {
        eventService.delete(UUID.fromString(id));
    }

    @GetMapping("/count")
    int count() { return eventService.count(); }

    @GetMapping("/name/{name}")
    List<EventDto> findByName(@PathVariable String name) {
        return eventService.findByName(name).stream().map(ExportDtoMethods::eventExport).toList();
    }
}
