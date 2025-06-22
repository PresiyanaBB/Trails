package com.trails_art.trails.dtos;

public record ArtistImportDto(
        String name,
        ImageDto image,
        String description,
        String instagram_url,
        ProjectData project,
        Boolean is_project_existing
) {
    public record ProjectData(
            String name,
            LocationDto location,
            ImageDto image,
            String youtube_url
    ) { }
}
