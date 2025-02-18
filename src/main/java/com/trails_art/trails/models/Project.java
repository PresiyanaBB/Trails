package com.trails_art.trails.models;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.*;

@Entity
public record Project(
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        UUID id,
        String name,
        @OneToOne(cascade = CascadeType.ALL)
        Location location,
        @OneToOne(cascade = CascadeType.ALL)
        Image image,
        String youtubeUrl,
        LocalDateTime created_on
) { }
