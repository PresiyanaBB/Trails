package com.trails_art.trails.services.event;

import com.trails_art.trails.dtos.EventDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.models.Event;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.repositories.JpaEventRepository;
import com.trails_art.trails.services.image.ImageService;
import com.trails_art.trails.services.location.LocationService;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JpaEventService implements EventService {

    private final JpaEventRepository jpaEventRepository;
    private final ImageService imageService;
    private final LocationService locationService;

    public JpaEventService(JpaEventRepository jpaEventRepository,
                           ImageService imageService,
                           LocationService locationService) {
        this.jpaEventRepository = jpaEventRepository;
        this.imageService = imageService;
        this.locationService = locationService;
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
        Image image = new Image(eventDto.image().mimetype(), Base64.getDecoder().decode(eventDto.image().data()));
        Location location = new Location(eventDto.location().name(),eventDto.location().map_address());
        Event event = new Event(eventDto.name(), eventDto.description(), image,eventDto.start_time(),eventDto.end_time(),location);
        imageService.create(image);
        locationService.create(location);
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
