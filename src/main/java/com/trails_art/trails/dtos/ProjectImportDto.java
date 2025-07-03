package com.trails_art.trails.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectImportDto(
        @NotNull(message = "Can't be null")
        @NotBlank(message = "Need to have minimum 1 non-white space character")
        String name,
        @Valid
        LocationDto location,
        @Valid
        ImageDto image,
        @NotNull(message = "Can't be null")
        @NotBlank(message = "Need to have minimum 1 non-white space character")
        String youtube_url,
        @Valid
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
