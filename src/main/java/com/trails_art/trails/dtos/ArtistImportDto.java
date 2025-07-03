package com.trails_art.trails.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ArtistImportDto(
        @NotNull(message = "Can't be null")
        @NotBlank(message = "Need to have minimum 1 non-white space character")
        String name,
        @Valid
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
