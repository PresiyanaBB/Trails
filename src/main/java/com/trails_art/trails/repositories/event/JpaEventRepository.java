package com.trails_art.trails.repositories.event;

import com.trails_art.trails.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaEventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByNameContainingIgnoreCase(String name);
}

