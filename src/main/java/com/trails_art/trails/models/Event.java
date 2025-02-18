package com.trails_art.trails.models;

import java.util.UUID;
import java.time.LocalDateTime;
import java.time.Duration;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.*;

@Entity
public record Event(
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        UUID id,
        String name,
        String description,
        @OneToOne(cascade = CascadeType.ALL)
        Image image,
        LocalDateTime start_time,
        LocalDateTime end_time,
        @OneToOne(cascade = CascadeType.ALL)
        Location location
) {
    public Event {
        if (!end_time.isAfter(start_time)) {
            throw new IllegalArgumentException("End time must be after Start time");
        }
    }

    public Duration getDuration() {
        return Duration.between(start_time,end_time);
    }

    public Boolean is_active() {
        return end_time.isAfter(LocalDateTime.now());
    }
}
