package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.EventDto;
import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.models.Event;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.services.event.JpaEventService;
import com.trails_art.trails.services.image.JpaImageService;
import com.trails_art.trails.services.location.JpaLocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final JpaEventService jpaEventService;
    private final JpaImageService jpaImageService;
    private final JpaLocationService jpaLocationService;

    EventController(JpaEventService jpaEventService, JpaImageService jpaImageService, JpaLocationService jpaLocationService) {
        this.jpaImageService = jpaImageService;
        this.jpaLocationService = jpaLocationService;
        this.jpaEventService = jpaEventService;
    }

    @GetMapping
    List<EventDto> findAll() {
        return jpaEventService.findAll().stream().map(this::eventExport).toList();
    }

    @GetMapping("/{id}")
    EventDto findById(@PathVariable UUID id) {
        Optional<Event> event = jpaEventService.findById(id);
        if(event.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found.");
        }
        return eventExport(event.get());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody EventDto eventDto) {
        Image image = new Image(eventDto.image().mimetype(), Base64.getDecoder().decode(eventDto.image().data()));
        Location location = new Location(eventDto.location().name(),eventDto.location().map_address());
        Event event = new Event(eventDto.name(), eventDto.description(), image,eventDto.start_time(),eventDto.end_time(),location);
        jpaImageService.create(image);
        jpaLocationService.create(location);
        jpaEventService.create(event);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody Event event, @PathVariable UUID id) {
        jpaEventService.update(event,id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable String id) {
        jpaEventService.delete(UUID.fromString(id));
    }

    @GetMapping("/count")
    int count() { return jpaEventService.count(); }

    @GetMapping("/name/{name}")
    List<EventDto> findByName(@PathVariable String name) {
        return jpaEventService.findByName(name).stream().map(this::eventExport).toList();
    }

    private EventDto eventExport(Event event) {
        ImageDto imageDto = new ImageDto(
                event.getImage().getMimetype(),
                Base64.getEncoder().encodeToString(event.getImage().getData())
        );

        LocationDto locationDto = new LocationDto(
                event.getLocation().getName(),
                event.getLocation().getMapAddress()
        );

        return new EventDto(
                event.getName(),
                event.getDescription(),
                imageDto,
                event.getStartTime(),
                event.getEndTime(),
                locationDto
        );
    }
}
