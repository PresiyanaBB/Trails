package com.trails_art.trails.repositories.artist_project;

import com.trails_art.trails.models.ArtistProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaArtistProjectRepository extends JpaRepository<ArtistProject, UUID> {
}
