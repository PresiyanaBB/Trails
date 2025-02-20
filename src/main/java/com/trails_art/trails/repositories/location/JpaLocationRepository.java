package com.trails_art.trails.repositories.location;

import com.trails_art.trails.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaLocationRepository extends JpaRepository<Location, UUID> {
    List<Location> findByNameContainingIgnoreCase(String name);
}

