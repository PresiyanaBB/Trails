package com.trails_art.trails.dtos;

import java.util.List;

public record ArtistExportDto(
        String name,
        ImageDto image,
        String description,
        String instagram_url,
        List<ProjectData> projects
) {
     public record ProjectData(
            String name,
            LocationDto location,
            ImageDto image,
            String youtube_url,
            String created_on
    ) { }
}
