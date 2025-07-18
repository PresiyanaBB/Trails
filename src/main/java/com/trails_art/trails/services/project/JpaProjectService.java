package com.trails_art.trails.services.project;

import com.trails_art.trails.dtos.ArtistDataDto;
import com.trails_art.trails.dtos.ProjectImportDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.mappers.ArtistMapper;
import com.trails_art.trails.mappers.ProjectMapper;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.models.Project;
import com.trails_art.trails.repositories.project.JpaProjectRepository;
import com.trails_art.trails.services.artist.ArtistService;
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
    private final ImageService imageService;
    private final LocationService locationService;

    public JpaProjectService(JpaProjectRepository jpaProjectRepository,
                             @Lazy ArtistService artistService,
                             ImageService imageService,
                             LocationService locationService) {
        this.jpaProjectRepository = jpaProjectRepository;
        this.artistService = artistService;
        this.imageService = imageService;
        this.locationService = locationService;
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
    public List<Project> findAllWithEmptyArtists() {
        return jpaProjectRepository.findAllWithEmptyArtists();
    }

    @Override
    public void create(Project project) {
        jpaProjectRepository.save(project);
    }

    @Override
    public Project createFromDto(ProjectImportDto projectImportDto){
        Project project = ProjectMapper.mapToProject(projectImportDto);

        if (!projectImportDto.is_artist_existing()) {
            handleNewArtist(projectImportDto.artist(), project);
        } else {
            handleExistingArtist(projectImportDto.artist(), project);
        }

        create(project);
        return project;
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
    public Project updateFromDto(ProjectImportDto projectImportDto, UUID id) {
         Project project = findById(id).orElseThrow(() -> new InvalidArgumentIdException("Project with ID " + id + " not found."));

         Image image = imageService.findById(project.getImage().getId()).orElseThrow(() -> new InvalidArgumentIdException("Image for project with ID " + project.getImage().getId() + " not found."));
         image.setMimetype(projectImportDto.image().mimetype());
         image.setData(Base64.getDecoder().decode(projectImportDto.image().data()));
         imageService.update(image, image.getId());

         Location location = locationService.findById(project.getLocation().getId()).orElseThrow(() -> new InvalidArgumentIdException("Location for project with ID " + project.getLocation().getId() + " not found."));
         location.setName(projectImportDto.location().name());
         location.setMapAddress(projectImportDto.location().map_address());
         locationService.update(location, location.getId());

         project.setName(projectImportDto.name());
         project.setImage(image);
         project.setLocation(location);
         project.setYoutubeUrl(projectImportDto.youtube_url());

         update(project, id);
         return project;
    }

    @Override
    public void delete(UUID id) {
        Project project = jpaProjectRepository.findById(id)
                .orElseThrow(() -> new InvalidArgumentIdException("Project with ID " + id + " not found."));

        List<Artist> artistsCopy = List.copyOf(project.getArtists());
        for (Artist a : artistsCopy) {
            a.setProjects(a.getProjects().stream().filter(p -> !p.getId().equals(id)).toList());

        }
        project.setArtists(List.of());
        update(project,id);
        jpaProjectRepository.deleteById(id);

        List<Artist> artists = artistService.findAllWithEmptyProjects();
        artists.forEach(a -> artistService.delete(a.getId()));
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
        Project project = jpaProjectRepository.findById(project_id).orElseThrow(() -> new InvalidArgumentIdException("Project with ID " + project_id + " not found."));
        project.getArtists().add(artist);
        artist.getProjects().add(project);
        artistService.update(artist, artist.getId());
        update(project, project.getId());
    }

    public void addExistingArtist(UUID project_id, UUID artist_id){
        Project project = jpaProjectRepository.findById(project_id).orElseThrow(() -> new InvalidArgumentIdException("Project with ID " + project_id + " not found."));
        Artist artist = artistService.findById(artist_id).orElseThrow(() -> new InvalidArgumentIdException("Artist with ID " + artist_id + " not found."));
        project.getArtists().add(artist);
        artist.getProjects().add(project);
        artistService.create(artist);
        update(project, project.getId());
    }

    public void deleteArtist(UUID project_id, UUID artist_id){
        Project project = jpaProjectRepository.findById(project_id).orElseThrow(() -> new InvalidArgumentIdException("Project with ID " + project_id + " not found."));
        Artist artist = artistService.findById(artist_id).orElseThrow(() -> new InvalidArgumentIdException("Artist with ID " + artist_id + " not found."));
        project.getArtists().remove(artist);
        artist.getProjects().remove(project);
        artistService.delete(artist_id);
        update(project, project.getId());
    }

    private void handleNewArtist(ArtistDataDto dto, Project project) {
        Artist artist = ArtistMapper.mapToArtist(dto);

        artist.getProjects().add(project);
        project.getArtists().add(artist);

        artistService.create(artist);
    }

    private void handleExistingArtist(ArtistDataDto dto, Project project) {
        List<Artist> artists = artistService.findByName(dto.name());

        for (Artist artist : artists) {
            boolean match = artist.getName().equals(dto.name()) &&
                    artist.getInstagramUrl().equals(dto.instagram_url());

            if (match) {
                artist.getProjects().add(project);
                project.getArtists().add(artist);
                artistService.update(artist, artist.getId());
            }
        }
    }

}
