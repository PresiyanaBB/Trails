package com.trails_art.trails.dtos;

import java.util.List;

public record ArtistExportDto(
        String id,
        String name,
        ImageDto image,
        String description,
        String instagram_url,
        List<ProjectDataDto> projects
) { }
