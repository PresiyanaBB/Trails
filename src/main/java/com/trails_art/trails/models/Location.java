package com.trails_art.trails.models;
import java.util.UUID;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public record Location(
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        UUID id,
        @NotEmpty(message = "Location cannot be empty")
        String name,
        String map_address
) { }
