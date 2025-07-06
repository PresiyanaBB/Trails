package com.trails_art.trails.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventDto(
        String id,
        @NotNull(message = "Can't be null")
        @NotBlank(message = "Need to have minimum 1 non-white space character")
        String name,
        String description,
        @Valid
        ImageDto image,
        LocalDateTime start_time,
        LocalDateTime end_time,
        @Valid
        LocationDto location
) { }
