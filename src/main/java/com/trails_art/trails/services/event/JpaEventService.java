package com.trails_art.trails.services.event;

import com.trails_art.trails.dtos.EventDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.exceptions.InvalidDTOFormat;
import com.trails_art.trails.mappers.EventMapper;
import com.trails_art.trails.mappers.ImageMapper;
import com.trails_art.trails.mappers.LocationMapper;
import com.trails_art.trails.models.Event;
import com.trails_art.trails.repositories.JpaEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JpaEventService implements EventService {

    private final JpaEventRepository jpaEventRepository;

    public JpaEventService(JpaEventRepository jpaEventRepository) {
        this.jpaEventRepository = jpaEventRepository;
    }

    @Override
    public List<Event> findAll() {
        return jpaEventRepository.findAll();
    }

    @Override
    public Optional<Event> findById(UUID id) {
        return jpaEventRepository.findById(id);
    }

    @Override
    public void create(Event event) {
        jpaEventRepository.save(event);
    }

    @Override
    public Event createFromDto(EventDto eventDto){
        Event event;
        try {
            event = EventMapper.mapToEvent(eventDto);
        } catch (Exception e) {
            throw new InvalidDTOFormat(e.getMessage() + "\nInvalid Event DTO");
        }

        create(event);
        return event;
    }

    @Override
    public void update(Event event, UUID id) {
        if (jpaEventRepository.existsById(id)) {
            event.setId(id);
            jpaEventRepository.save(event);
        } else {
            throw new InvalidArgumentIdException("Event with ID " + id + " not found.");
        }
    }

    @Override
    public Event updateFromDto(EventDto eventDto, UUID id){
        Event event = jpaEventRepository.findById(id).orElseThrow(() -> new InvalidArgumentIdException("Event with ID " + id + " not found."));
        event.setName(eventDto.name());
        event.setDescription(eventDto.description());
        event.setLocation(LocationMapper.mapToLocation(eventDto.location()));
        event.setImage(ImageMapper.mapToImage(eventDto.image()));
        event.setStartTime(eventDto.start_time());
        event.setEndTime(eventDto.end_time());
        update(event, id);
        return event;
    }

    @Override
    public void delete(UUID id) {
        jpaEventRepository.findById(id).orElseThrow(() -> new InvalidArgumentIdException("Event with ID " + id + " not found."));
        jpaEventRepository.deleteById(id);
    }

    @Override
    public int count() {
        return (int) jpaEventRepository.count();
    }

    @Override
    public void saveAll(List<Event> events) {
        jpaEventRepository.saveAll(events);
    }

    @Override
    public List<Event> findByName(String name) {
        return jpaEventRepository.findByNameContainingIgnoreCase(name);
    }
}
