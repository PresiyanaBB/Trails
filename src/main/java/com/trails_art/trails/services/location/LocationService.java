package com.trails_art.trails.services.location;

import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.models.Location;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LocationService {
    List<Location> findAll();

    Optional<Location> findById(UUID id);

    void create(Location location);

    void createFromDto(LocationDto locationDto);

    void update(Location location, UUID id);

    void updateFromDto(LocationDto locationDto, UUID id);

    void delete(UUID id);

    int count();

    void saveAll(List<Location> locations);

    List<Location> findByName(String name);
}
