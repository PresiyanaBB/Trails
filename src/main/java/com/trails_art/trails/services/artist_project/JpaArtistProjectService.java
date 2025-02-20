package com.trails_art.trails.services.artist_project;

import com.trails_art.trails.models.ArtistProject;
import com.trails_art.trails.repositories.artist_project.JpaArtistProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JpaArtistProjectService implements ArtistProjectService {

    private final JpaArtistProjectRepository jpaArtistProjectRepository;

    public JpaArtistProjectService(JpaArtistProjectRepository jpaArtistProjectRepository) {
        this.jpaArtistProjectRepository = jpaArtistProjectRepository;
    }

    @Override
    public List<ArtistProject> findAll() {
        return jpaArtistProjectRepository.findAll();
    }

    @Override
    public Optional<ArtistProject> findById(UUID id) {
        return jpaArtistProjectRepository.findById(id);
    }

    @Override
    public void create(ArtistProject artistProject) {
        jpaArtistProjectRepository.save(artistProject);
    }

    @Override
    public void update(ArtistProject artistProject, UUID id) {
        if (jpaArtistProjectRepository.existsById(id)) {
            artistProject.setId(id);
            jpaArtistProjectRepository.save(artistProject);
        } else {
            throw new IllegalArgumentException("ArtistProject with ID " + id + " not found");
        }
    }

    @Override
    public void delete(UUID id) {
        jpaArtistProjectRepository.deleteById(id);
    }

    @Override
    public int count() {
        return (int) jpaArtistProjectRepository.count();
    }

    @Override
    public void saveAll(List<ArtistProject> artistProjects) {
        jpaArtistProjectRepository.saveAll(artistProjects);
    }
}

