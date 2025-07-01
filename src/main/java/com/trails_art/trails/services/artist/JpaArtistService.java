package com.trails_art.trails.services.artist;

import com.trails_art.trails.dtos.ArtistImportDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.mappers.ArtistMapper;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.ArtistProject;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Project;
import com.trails_art.trails.repositories.artist.JpaArtistRepository;
import com.trails_art.trails.services.artist_project.ArtistProjectService;
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
    private final ArtistProjectService artistProjectService;
    private final ImageService imageService;
    private final LocationService locationService;
    private final ArtistMapper artistMapper;

    public JpaArtistService(JpaArtistRepository jpaArtistRepository,
                            @Lazy ProjectService projectService,
                            ArtistProjectService artistProjectService,
                            ImageService imageService,
                            LocationService locationService,
                            ArtistMapper artistMapper) {
        this.jpaArtistRepository = jpaArtistRepository;
        this.projectService = projectService;
        this.artistProjectService = artistProjectService;
        this.imageService = imageService;
        this.locationService = locationService;
        this.artistMapper = artistMapper;
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
    public void createFromDto(ArtistImportDto artistImportDto){
        Artist artist = artistMapper.mapToArtist(artistImportDto);
        jpaArtistRepository.save(artist);
        imageService.create(artist.getImage());

        if (!artistImportDto.is_project_existing()) {
            handleNewProject(artistImportDto.project(), artist);
        } else {
            handleExistingProject(artistImportDto.project(), artist);
        }

        update(artist, artist.getId());
    }

    @Override
    public void update(Artist artist, UUID id) {
        if (jpaArtistRepository.existsById(id)) {
            artist.setId(id);
            jpaArtistRepository.save(artist);
        } else {
            throw new InvalidArgumentIdException("Artist with ID " + id + " not found");
        }
    }

    @Override
    public void updateFromDto(ArtistImportDto artistImportDto, UUID id){
        Artist artist = findById(id)
                .orElseThrow(() -> new InvalidArgumentIdException("Artist with ID " + id + " not found"));

        artist.setName(artistImportDto.name());
        artist.setDescription(artistImportDto.description());
        artist.setInstagramUrl(artistImportDto.instagram_url());

        Image image = imageService.findById(artist.getImage().getId())
                .orElseThrow(() -> new IllegalArgumentException("Image for Artist not found"));
        image.setMimetype(artistImportDto.image().mimetype());
        image.setData(Base64.getDecoder().decode(artistImportDto.image().data()));
        artist.setImage(image);

        imageService.update(image,image.getId());
        update(artist, id);
    }

    @Override
    public void delete(UUID id) {
        Artist artist = jpaArtistRepository.findById(id).orElseThrow();
        jpaArtistRepository.deleteById(id);
        List<Project> projects = projectService.findAll();
        projects.forEach(project -> {
            if(project.getArtistProjects().isEmpty()) {
                projectService.delete(project.getId());
            }
        });
        artistProjectService.deleteAll(artist.getArtistProjects());
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

    @Override
    public void addProjects(List<UUID> projects, UUID artistId) {
        Artist artist = jpaArtistRepository.findById(artistId).orElseThrow(() -> new InvalidArgumentIdException("Artist with ID " + artistId + " not found"));
        List<Project> projectList = projectService.findAllByIdIn(projects).stream().toList();
        projectList.forEach(project -> {
            ArtistProject ap = new ArtistProject(artist, project);
            project.getArtistProjects().add(ap);
            artist.getArtistProjects().add(ap);
            artistProjectService.create(ap);
            projectService.update(project, project.getId());
            update(artist, artistId);
        });
    }

    private void handleNewProject(ArtistImportDto.ProjectData dto, Artist artist) {
        Project project = artistMapper.mapToProject(dto);
        ArtistProject artistProject = new ArtistProject(artist, project);

        imageService.create(project.getImage());
        locationService.create(project.getLocation());
        projectService.create(project);
        artistProjectService.create(artistProject);

        artist.getArtistProjects().add(artistProject);
        project.getArtistProjects().add(artistProject);
        projectService.update(project, project.getId());
    }

    private void handleExistingProject(ArtistImportDto.ProjectData dto, Artist artist) {
        List<Project> projects = projectService.findByName(dto.name());

        for (Project pr : projects) {
            boolean match = pr.getLocation().getName().equalsIgnoreCase(dto.location().name()) &&
                    pr.getLocation().getMapAddress().equalsIgnoreCase(dto.location().map_address());

            if (match) {
                ArtistProject artistProject = new ArtistProject(artist, pr);
                artistProjectService.create(artistProject);
                artist.getArtistProjects().add(artistProject);
                pr.getArtistProjects().add(artistProject);
                projectService.update(pr, pr.getId());
            }
        }
    }

}
