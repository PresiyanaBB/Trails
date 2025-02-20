package com.trails_art.trails.services.artist;

import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Project;
import com.trails_art.trails.repositories.artist.JpaArtistRepository;
import com.trails_art.trails.repositories.artist_project.JpaArtistProjectRepository;
import com.trails_art.trails.repositories.project.JpaProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JpaArtistService implements ArtistService {

    private final JpaArtistRepository jpaArtistRepository;
    private final JpaProjectRepository jpaProjectRepository;
    private final JpaArtistProjectRepository jpaArtistProjectRepository;

    public JpaArtistService(JpaArtistRepository jpaArtistRepository, JpaProjectRepository jpaProjectRepository, JpaArtistProjectRepository jpaArtistProjectRepository) {
        this.jpaArtistRepository = jpaArtistRepository;
        this.jpaProjectRepository = jpaProjectRepository;
        this.jpaArtistProjectRepository = jpaArtistProjectRepository;
    }

    @Override
    public List<Artist> findAll() {
        return jpaArtistRepository.findAll();
    }

    @Override
    public Optional<Artist> findById(UUID id) {
        return jpaArtistRepository.findById(id);
    }

    @Override
    public void create(Artist artist) {
        jpaArtistRepository.save(artist);
    }

    @Override
    public void update(Artist artist, UUID id) {
        if (jpaArtistRepository.existsById(id)) {
            artist.setId(id);
            jpaArtistRepository.save(artist);
        } else {
            throw new IllegalArgumentException("Artist with ID " + id + " not found");
        }
    }

    @Override
    public void delete(UUID id) {
        Artist artist = jpaArtistRepository.findById(id).orElseThrow();
        jpaArtistRepository.deleteById(id);
        List<Project> projects = jpaProjectRepository.findAll();
        projects.forEach(project -> {
            if(project.getArtistProjects().isEmpty()) {
                jpaProjectRepository.delete(project);
            }
        });
        jpaArtistProjectRepository.deleteAll(artist.getArtistProjects());
    }

    @Override
    public int count() {
        return (int) jpaArtistRepository.count();
    }

    @Override
    public void saveAll(List<Artist> artists) {
        jpaArtistRepository.saveAll(artists);
    }

    @Override
    public List<Artist> findByName(String name) {
        return jpaArtistRepository.findByNameContainingIgnoreCase(name);
    }
}
