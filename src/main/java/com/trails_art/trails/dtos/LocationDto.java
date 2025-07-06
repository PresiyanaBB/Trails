package com.trails_art.trails.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocationDto(
        String id,
        @NotNull(message = "Can't be null")
        @NotBlank(message = "Need to have minimum 1 non-white space character")
        String name,
        @NotNull(message = "Can't be null")
        @NotBlank(message = "Need to have minimum 1 non-white space character")
        String map_address
) { }
