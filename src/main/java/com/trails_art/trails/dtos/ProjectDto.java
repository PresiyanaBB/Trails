package com.trails_art.trails.dtos;

public record ProjectDto(
        String name,
        LocationDto location,
        ImageDto image,
        String youtube_url,
        ArtistData[] artists
) {
    public record ArtistData(
            String name,
            ImageDto image,
            String description,
            String instagram_url
    ) { }
}
