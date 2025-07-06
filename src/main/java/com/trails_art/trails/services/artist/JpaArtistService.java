package com.trails_art.trails.services.artist;

import com.trails_art.trails.dtos.ArtistImportDto;
import com.trails_art.trails.dtos.ProjectDataDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.mappers.ArtistMapper;
import com.trails_art.trails.mappers.ProjectMapper;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Project;
import com.trails_art.trails.repositories.artist.JpaArtistRepository;
import com.trails_art.trails.services.image.ImageService;
import com.trails_art.trails.services.location.LocationService;
import com.trails_art.trails.services.project.ProjectService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JpaArtistService implements ArtistService {

    private final JpaArtistRepository jpaArtistRepository;
    private final ProjectService projectService;
    private final ImageService imageService;
    private final LocationService locationService;

    public JpaArtistService(JpaArtistRepository jpaArtistRepository,
                            @Lazy ProjectService projectService,
                            ImageService imageService,
                            LocationService locationService) {
        this.jpaArtistRepository = jpaArtistRepository;
        this.projectService = projectService;
        this.imageService = imageService;
        this.locationService = locationService;
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
    public List<Artist> findAllWithEmptyProjects() {
        return jpaArtistRepository.findAllWithEmptyProjects();
    }

    @Override
    public void create(Artist artist) {
        jpaArtistRepository.save(artist);
    }

    @Override
    public Artist createFromDto(ArtistImportDto artistImportDto){
        Artist artist = ArtistMapper.mapToArtist(artistImportDto);

        if (!artistImportDto.is_project_existing()) {
            handleNewProject(artistImportDto.project(), artist);
        } else {
            handleExistingProject(artistImportDto.project(), artist);
        }

        create(artist);
        return artist;
    }

    @Override
    public void update(Artist artist, UUID id) {
        if (jpaArtistRepository.existsById(id)) {
            artist.setId(id);
            jpaArtistRepository.save(artist);
        } else {
            throw new InvalidArgumentIdException("Artist with ID " + id + " not found.");
        }
    }

    @Override
    public Artist updateFromDto(ArtistImportDto artistImportDto, UUID id){
        Artist artist = findById(id)
                .orElseThrow(() -> new InvalidArgumentIdException("Artist with ID " + id + " not found."));

        artist.setName(artistImportDto.name());
        artist.setDescription(artistImportDto.description());
        artist.setInstagramUrl(artistImportDto.instagram_url());

        Image image = imageService.findById(artist.getImage().getId())
                .orElseThrow(() -> new IllegalArgumentException("Image for Artist not found."));
        image.setMimetype(artistImportDto.image().mimetype());
        image.setData(Base64.getDecoder().decode(artistImportDto.image().data()));
        artist.setImage(image);

        imageService.update(image,image.getId());
        update(artist, id);

        return artist;
    }

    @Override
    public void delete(UUID id) {
        Artist artist = jpaArtistRepository.findById(id)
                .orElseThrow(() -> new InvalidArgumentIdException("Artist with ID " + id + " not found."));

        List<Project> projectsCopy = List.copyOf(artist.getProjects());
        for (Project project : projectsCopy) {
            project.setArtists(project.getArtists().stream().filter(a -> !a.getId().equals(id)).toList());

        }
        artist.setProjects(List.of());
        update(artist,id);
        jpaArtistRepository.deleteById(id);

        List<Project> projects = projectService.findAllWithEmptyArtists();
        projects.forEach(project -> projectService.delete(project.getId()));
    }

    @Override
    public int count() {
        return (int) jpaArtistRepository.count();
    }

    @Override
    public List<Artist> findByName(String name) {
        return jpaArtistRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public void addProjects(List<UUID> projects, UUID artistId) {
        Artist artist = jpaArtistRepository.findById(artistId).orElseThrow(() -> new InvalidArgumentIdException("Artist with ID " + artistId + " not found."));
        List<Project> projectList = projectService.findAllByIdIn(projects).stream().toList();
        projectList.forEach(project -> {
            project.getArtists().add(artist);
            artist.getProjects().add(project);
            projectService.update(project, project.getId());
            update(artist, artistId);
        });
    }

    private void handleNewProject(ProjectDataDto dto, Artist artist) {
        Project project = ProjectMapper.mapToProject(dto);

        imageService.create(project.getImage());
        locationService.create(project.getLocation());

        artist.getProjects().add(project);
        project.getArtists().add(artist);
        projectService.create(project);
    }

    private void handleExistingProject(ProjectDataDto dto, Artist artist) {
        List<Project> projects = projectService.findByName(dto.name());

        for (Project pr : projects) {
            boolean match = pr.getLocation().getName().equalsIgnoreCase(dto.location().name()) &&
                    pr.getLocation().getMapAddress().equalsIgnoreCase(dto.location().map_address());

            if (match) {
                artist.getProjects().add(pr);
                pr.getArtists().add(artist);
                projectService.update(pr, pr.getId());
            }
        }
    }

}
