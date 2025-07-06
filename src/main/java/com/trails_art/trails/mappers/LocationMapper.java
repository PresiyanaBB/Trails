package com.trails_art.trails.mappers;

import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.exceptions.InvalidDTOFormat;
import com.trails_art.trails.models.Location;

public class LocationMapper {
    public static Location mapToLocation(LocationDto dto) {
        Location location;
        try {
            location = new Location(dto.name(), dto.map_address());
        } catch (Exception e) {
            throw new InvalidDTOFormat("Location DTO is not valid");
        }

        return location;
    }

    public static LocationDto mapToLocationDto(Location location) {
        return new LocationDto(location.getId().toString(),
                location.getName(),
                location.getMapAddress());
    }

}
