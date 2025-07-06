package com.trails_art.trails.dtos;

public record ProjectDataDto(
        String name,
        LocationDto location,
        ImageDto image,
        String youtube_url,
        String created_on
) { }
