package com.trails_art.trails.services.event;

import com.trails_art.trails.dtos.EventDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.exceptions.InvalidDTOFormat;
import com.trails_art.trails.models.Event;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.repositories.JpaEventRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;
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
    public void createFromDto(EventDto eventDto){
        Image image;
        Location location;
        Event event;
        try {
            image = new Image(eventDto.image().mimetype(), Base64.getDecoder().decode(eventDto.image().data()));
            location = new Location(eventDto.location().name(),eventDto.location().map_address());
            event = new Event(eventDto.name(), eventDto.description(), image,eventDto.start_time(),eventDto.end_time(),location);
        } catch (Exception e) {
            throw new InvalidDTOFormat(e.getMessage() + "\nInvalid Event DTO");
        }

        create(event);
    }

    @Override
    public void update(Event event, UUID id) {
        if (jpaEventRepository.existsById(id)) {
            event.setId(id);
            jpaEventRepository.save(event);
        } else {
            throw new InvalidArgumentIdException("Event with ID " + id + " not found");
        }
    }

    @Override
    public void delete(UUID id) {
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
