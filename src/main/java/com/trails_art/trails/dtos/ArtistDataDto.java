package com.trails_art.trails.dtos;

public record ArtistDataDto(
        String name,
        ImageDto image,
        String description,
        String instagram_url
) { }
