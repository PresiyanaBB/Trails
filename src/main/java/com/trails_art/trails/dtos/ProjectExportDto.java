package com.trails_art.trails.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectExportDto(
        String name,
        LocationDto location,
        ImageDto image,
        String youtube_url,
        LocalDateTime created_on,
        List<ArtistData> artists
) {
     public record ArtistData(
            String name,
            ImageDto image,
            String description,
            String instagram_url
    ) { }
}
