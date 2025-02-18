package com.trails_art.trails.modules;
import java.util.UUID;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public record Image(
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        UUID id,
        String MIMEType,
        @NotNull
        byte[] data
) { }
