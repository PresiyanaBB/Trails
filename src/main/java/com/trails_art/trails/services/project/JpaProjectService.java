package com.trails_art.trails.services.project;

import com.trails_art.trails.dtos.ProjectImportDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.mappers.ProjectMapper;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.ArtistProject;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.models.Project;
import com.trails_art.trails.repositories.project.JpaProjectRepository;
import com.trails_art.trails.services.artist.ArtistService;
import com.trails_art.trails.services.artist_project.ArtistProjectService;
import com.trails_art.trails.services.image.ImageService;
import com.trails_art.trails.services.location.LocationService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JpaProjectService implements ProjectService {

    private final JpaProjectRepository jpaProjectRepository;
    private final ArtistService artistService;
    private final ArtistProjectService artistProjectService;
    private final ImageService imageService;
    private final LocationService locationService;
    private final ProjectMapper projectMapper;

    public JpaProjectService(JpaProjectRepository jpaProjectRepository,
                             @Lazy ArtistService artistService,
                             ArtistProjectService artistProjectService,
                             ImageService imageService,
                             LocationService locationService,
                             ProjectMapper projectMapper) {
        this.jpaProjectRepository = jpaProjectRepository;
        this.artistService = artistService;
        this.artistProjectService = artistProjectService;
        this.imageService = imageService;
        this.locationService = locationService;
        this.projectMapper = projectMapper;
    }

    @Override
    public List<Project> findAll() {
        return jpaProjectRepository.findAll();
    }

    @Override
    public List<Project> findAllByIdIn(List<UUID> ids) { return jpaProjectRepository.findAllById(ids); }

    @Override
    public Optional<Project> findById(UUID id) {
        return jpaProjectRepository.findById(id);
    }

    @Override
    public void create(Project project) {
        jpaProjectRepository.save(project);
    }

    @Override
    public void createFromDto(ProjectImportDto projectImportDto){
        Project project = projectMapper.mapToProject(projectImportDto);
        create(project);
        imageService.create(project.getImage());
        locationService.create(project.getLocation());

        if (!projectImportDto.is_artist_existing()) {
            handleNewArtist(projectImportDto.artist(), project);
        } else {
            handleExistingArtist(projectImportDto.artist(), project);
        }

        update(project, project.getId());
    }

    @Override
    public void update(Project project, UUID id) {
        if (jpaProjectRepository.existsById(id)) {
            project.setId(id);
            jpaProjectRepository.save(project);
        } else {
            throw new InvalidArgumentIdException("Project with ID " + id + " not found.");
        }
    }

    @Override
    public void updateFromDto(ProjectImportDto projectImportDto, UUID id) {
         Project project = findById(id).orElseThrow();

         Image image = imageService.findById(project.getImage().getId()).orElseThrow();
         image.setMimetype(projectImportDto.image().mimetype());
         image.setData(Base64.getDecoder().decode(projectImportDto.image().data()));
         imageService.update(image, image.getId());

         Location location = locationService.findById(project.getLocation().getId()).orElseThrow();
         location.setName(projectImportDto.location().name());
         location.setMapAddress(projectImportDto.location().map_address());
         locationService.update(location, location.getId());

         project.setName(projectImportDto.name());
         project.setImage(image);
         project.setLocation(location);
         project.setYoutubeUrl(projectImportDto.youtube_url());

         update(project, id);
    }

    @Override
    public void delete(UUID id) {
        Project project = jpaProjectRepository.findById(id).orElseThrow();
        jpaProjectRepository.deleteById(id);
        List<Artist> artists = artistService.findAll();
        artists.forEach(artist -> {
            if(artist.getArtistProjects().isEmpty()) {
                artistService.delete(artist.getId());
            }
        });
        artistProjectService.deleteAll(project.getArtistProjects());
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
        artistService.create(artist);
        artistProjectService.create(artistProject);
    }

    public void addExistingArtist(UUID project_id, UUID artist_id){
        Project project = jpaProjectRepository.findById(project_id).orElseThrow();
        Artist artist = artistService.findById(artist_id).orElseThrow();
        ArtistProject artistProject = new ArtistProject(artist, project);
        project.getArtistProjects().add(artistProject);
        artist.getArtistProjects().add(artistProject);
        artistService.create(artist);
        artistProjectService.create(artistProject);
    }

    public void deleteArtist(UUID project_id, UUID artist_id){
        Project project = jpaProjectRepository.findById(project_id).orElseThrow();
        Artist artist = artistService.findById(artist_id).orElseThrow();
        ArtistProject artistProject = artistProjectService.findAll().stream()
                .filter(ap -> ap.getArtist().getId() == artist_id && ap.getProject().getId() == project_id)
                .findFirst()
                .orElseThrow();
        project.getArtistProjects().remove(artistProject);
        artist.getArtistProjects().remove(artistProject);
        artistProjectService.delete(artistProject.getId());
    }

    private void handleNewArtist(ProjectImportDto.ArtistData dto, Project project) {
        Artist artist = projectMapper.mapToArtist(dto);
        ArtistProject artistProject = new ArtistProject(artist, project);

        imageService.create(artist.getImage());
        artistService.create(artist);
        artistProjectService.create(artistProject);

        artist.getArtistProjects().add(artistProject);
        project.getArtistProjects().add(artistProject);
        artistService.update(artist, artist.getId());
    }

    private void handleExistingArtist(ProjectImportDto.ArtistData dto, Project project) {
        List<Artist> artists = artistService.findByName(dto.name());

        for (Artist artist : artists) {
            boolean match = artist.getName().equals(dto.name()) &&
                    artist.getInstagramUrl().equals(dto.instagram_url());

            if (match) {
                ArtistProject artistProject = new ArtistProject(artist, project);
                artistProjectService.create(artistProject);
                artist.getArtistProjects().add(artistProject);
                project.getArtistProjects().add(artistProject);
                artistService.update(artist, artist.getId());
            }
        }
    }

}
