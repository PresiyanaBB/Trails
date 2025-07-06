package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.EventDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.mappers.EventMapper;
import com.trails_art.trails.models.Event;
import com.trails_art.trails.services.event.EventService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> findAll() {
        List<EventDto> dtos = eventService.findAll().stream()
                .map(EventMapper::mapToEventDto)
                .toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(dtos.size()))
                .body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> findById(@PathVariable UUID id) {
        Event event = eventService.findById(id)
                .orElseThrow(() -> new InvalidArgumentIdException("Event not found."));
        return ResponseEntity.ok(EventMapper.mapToEventDto(event));
    }

    @GetMapping(params = "name")
    public ResponseEntity<List<EventDto>> findByName(@RequestParam String name) {
        List<EventDto> dtos = eventService.findByName(name).stream()
                .map(EventMapper::mapToEventDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<EventDto> create(@Valid @RequestBody EventDto dto) {
        Event event = eventService.createFromDto(dto);
        URI location = URI.create("/api/events/" + event.getId());
        return ResponseEntity.created(location)
                .body(EventMapper.mapToEventDto(event));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<EventDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody EventDto dto
    ) {
        Event event = eventService.updateFromDto(dto, id);
        return ResponseEntity.ok(EventMapper.mapToEventDto(event));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
