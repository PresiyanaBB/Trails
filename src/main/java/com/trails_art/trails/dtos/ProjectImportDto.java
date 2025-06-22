package com.trails_art.trails.dtos;

public record ProjectImportDto(
        String name,
        LocationDto location,
        ImageDto image,
        String youtube_url,
        ArtistData artist,
        Boolean is_artist_existing
) {
    public record ArtistData(
            String name,
            ImageDto image,
            String description,
            String instagram_url
    ) { }
}
