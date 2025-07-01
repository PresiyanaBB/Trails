package com.trails_art.trails.repositories.artist;

import com.trails_art.trails.models.Artist;

import java.util.List;

public interface CustomArtistRepository {
    List<Artist> findAllWithEmptyProjects();
}
