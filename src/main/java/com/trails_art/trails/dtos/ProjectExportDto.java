package com.trails_art.trails.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectExportDto(
        String id,
        String name,
        LocationDto location,
        ImageDto image,
        String youtube_url,
        LocalDateTime created_on,
        List<ArtistDataDto> artists
) { }
