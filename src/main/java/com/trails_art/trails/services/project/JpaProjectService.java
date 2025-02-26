package com.trails_art.trails.services.project;

import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.ArtistProject;
import com.trails_art.trails.models.Project;
import com.trails_art.trails.repositories.artist.JpaArtistRepository;
import com.trails_art.trails.repositories.artist_project.JpaArtistProjectRepository;
import com.trails_art.trails.repositories.project.JpaProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JpaProjectService implements ProjectService {

    private final JpaProjectRepository jpaProjectRepository;
    private final JpaArtistRepository jpaArtistRepository;
    private final JpaArtistProjectRepository jpaArtistProjectRepository;

    public JpaProjectService(JpaProjectRepository jpaProjectRepository, JpaArtistRepository jpaArtistRepository, JpaArtistProjectRepository jpaArtistProjectRepository) {
        this.jpaProjectRepository = jpaProjectRepository;
        this.jpaArtistRepository = jpaArtistRepository;
        this.jpaArtistProjectRepository = jpaArtistProjectRepository;
    }

    @Override
    public List<Project> findAll() {
        return jpaProjectRepository.findAll();
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return jpaProjectRepository.findById(id);
    }

    @Override
    public void create(Project project) {
        jpaProjectRepository.save(project);
    }

    @Override
    public void update(Project project, UUID id) {
        if (jpaProjectRepository.existsById(id)) {
            project.setId(id);
            jpaProjectRepository.save(project);
        } else {
            throw new IllegalArgumentException("Project with ID " + id + " not found.");
        }
    }

    @Override
    public void delete(UUID id) {
        Project project = jpaProjectRepository.findById(id).orElseThrow();
        jpaProjectRepository.deleteById(id);
        List<Artist> artists = jpaArtistRepository.findAll();
        artists.forEach(artist -> {
            if(artist.getArtistProjects().isEmpty()) {
                jpaArtistRepository.delete(artist);
            }
        });
        jpaArtistProjectRepository.deleteAll(project.getArtistProjects());
    }

    @Override
    public int count() {
        return (int) jpaProjectRepository.count();
    }

    @Override
    public void saveAll(List<Project> projects) {
        jpaProjectRepository.saveAll(projects);
    }

    @Override
    public List<Project> findByName(String name) {
        return jpaProjectRepository.findByNameContainingIgnoreCase(name);
    }

    public void addNonExistingArtist(UUID project_id, Artist artist){
        Project project = jpaProjectRepository.findById(project_id).orElseThrow();
        ArtistProject artistProject = new ArtistProject(artist, project);
        project.getArtistProjects().add(artistProject);
        artist.getArtistProjects().add(artistProject);
        jpaArtistRepository.save(artist);
        jpaArtistProjectRepository.save(artistProject);
    }

    public void addExistingArtist(UUID project_id, UUID artist_id){
        Project project = jpaProjectRepository.findById(project_id).orElseThrow();
        Artist artist = jpaArtistRepository.findById(artist_id).orElseThrow();
        ArtistProject artistProject = new ArtistProject(artist, project);
        project.getArtistProjects().add(artistProject);
        artist.getArtistProjects().add(artistProject);
        jpaArtistRepository.save(artist);
        jpaArtistProjectRepository.save(artistProject);
    }

    public void deleteArtist(UUID project_id, UUID artist_id){
        Project project = jpaProjectRepository.findById(project_id).orElseThrow();
        Artist artist = jpaArtistRepository.findById(artist_id).orElseThrow();
        ArtistProject artistProject = jpaArtistProjectRepository.findAll().stream()
                .filter(ap -> ap.getArtist().getId() == artist_id && ap.getProject().getId() == project_id)
                .findFirst()
                .orElseThrow();
        project.getArtistProjects().remove(artistProject);
        artist.getArtistProjects().remove(artistProject);
        jpaArtistProjectRepository.delete(artistProject);
    }
}
