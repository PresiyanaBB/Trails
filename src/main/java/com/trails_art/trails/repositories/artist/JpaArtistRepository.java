package com.trails_art.trails.repositories.artist;

import com.trails_art.trails.models.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaArtistRepository extends JpaRepository<Artist, UUID> {
    List<Artist> findByNameContainingIgnoreCase(String name);
}
