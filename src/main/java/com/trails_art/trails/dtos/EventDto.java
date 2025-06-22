package com.trails_art.trails.dtos;

import java.time.LocalDateTime;

public record EventDto(
        String id,
        String name,
        String description,
        ImageDto image,
        LocalDateTime start_time,
        LocalDateTime end_time,
        LocationDto location
) { }
