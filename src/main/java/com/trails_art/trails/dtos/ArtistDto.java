package com.trails_art.trails.dtos;

public record ArtistDto(
        String name,
        ImageDto image,
        String description,
        String instagram_url,
        ProjectData[] projects
) {
    public record ProjectData(
            String name,
            LocationDto location,
            ImageDto image,
            String youtube_url
    ) { }
}
