package com.trails_art.trails.services.event;

import com.trails_art.trails.dtos.EventDto;
import com.trails_art.trails.models.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventService {
    List<Event> findAll();

    Optional<Event> findById(UUID id);

    void create(Event event);

    Event createFromDto(EventDto eventDto);

    void update(Event event, UUID id);

    Event updateFromDto(EventDto eventDto, UUID id);

    void delete(UUID id);

    int count();

    void saveAll(List<Event> events);

    List<Event> findByName(String name);
}
