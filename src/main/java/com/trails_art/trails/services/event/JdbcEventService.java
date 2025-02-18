package com.trails_art.trails.services.event;

import com.trails_art.trails.models.Event;
import com.trails_art.trails.repositories.event.JdbcEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JdbcEventService implements EventService {
    private final JdbcEventRepository jdbcEventRepository;

    public JdbcEventService(JdbcEventRepository jdbcEventRepository) {
        this.jdbcEventRepository = jdbcEventRepository;
    }

    public List<Event> findAll() {
        return jdbcEventRepository.findAll();
    }

    public Optional<Event> findById(UUID id) {
        return jdbcEventRepository.findById(id);
    }

    public void create(Event event) {
        jdbcEventRepository.create(event);
    }

    public void update(Event event, UUID id){
        jdbcEventRepository.update(event, id);
    }

    public void delete(UUID id){
        jdbcEventRepository.delete(id);
    }

    public int count(){
        return jdbcEventRepository.count();
    }

    public void saveAll(List<Event> events){
        jdbcEventRepository.saveAll(events);
    }

    public List<Event> findByName(String name){
        return jdbcEventRepository.findByName(name);
    }
}
