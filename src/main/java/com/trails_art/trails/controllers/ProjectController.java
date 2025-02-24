package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.dtos.ProjectImportDto;
import com.trails_art.trails.dtos.ProjectExportDto;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.ArtistProject;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.models.Project;
import com.trails_art.trails.services.artist.JpaArtistService;
import com.trails_art.trails.services.artist_project.JpaArtistProjectService;
import com.trails_art.trails.services.image.JpaImageService;
import com.trails_art.trails.services.location.JpaLocationService;
import com.trails_art.trails.services.project.JpaProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final JpaProjectService jpaProjectService;
    private final JpaArtistService jpaArtistService;
    private final JpaImageService jpaImageService;
    private final JpaLocationService jpaLocationService;
    private final JpaArtistProjectService jpaArtistProjectService;

    ProjectController(JpaArtistService jpaArtistService, JpaProjectService jpaProjectService,
                      JpaImageService jpaImageService, JpaLocationService jpaLocationService,
                      JpaArtistProjectService jpaArtistProjectService) {
        this.jpaArtistService = jpaArtistService;
        this.jpaProjectService = jpaProjectService;
        this.jpaImageService = jpaImageService;
        this.jpaLocationService = jpaLocationService;
        this.jpaArtistProjectService = jpaArtistProjectService;
    }

    @GetMapping
    List<ProjectExportDto> findAll() {
        return jpaProjectService.findAll().stream().map(this::exportProject).toList();
    }

    @GetMapping("/{id}")
    ProjectExportDto findById(@PathVariable UUID id) {
        Optional<Project> project = jpaProjectService.findById(id);
        if(project.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found.");
        }
        return exportProject(project.get());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody ProjectImportDto projectImportDto) {
        Image image_project = new Image(projectImportDto.image().mimetype(),Base64.getDecoder().decode(projectImportDto.image().data()));
        Location location = new Location(projectImportDto.location().name(), projectImportDto.location().map_address());
        Project project = new Project(projectImportDto.name(),location,image_project, projectImportDto.youtube_url());
        jpaImageService.create(image_project);
        jpaLocationService.create(location);
        jpaProjectService.create(project);

        int size = projectImportDto.artists().size();
        for (int i = 0; i < size; i++) {
            ProjectImportDto.ArtistData current_artist = projectImportDto.artists().get(i);
            if(!projectImportDto.is_artist_existing().get(i)) {
                Image image_artist = new Image(current_artist.image().mimetype(), Base64.getDecoder().decode(current_artist.image().data()));
                Artist artist = new Artist(current_artist.name(),image_artist, current_artist.description(), current_artist.instagram_url());
                ArtistProject artistProject = new ArtistProject(artist,project);
                jpaImageService.create(image_artist);
                jpaArtistService.create(artist);
                jpaArtistProjectService.create(artistProject);
                artist.getArtistProjects().add(artistProject);
                project.getArtistProjects().add(artistProject);
                jpaArtistService.update(artist,artist.getId());
            }
            else {
                List<Artist> artists = jpaArtistService.findByName(current_artist.name());
                artists.forEach(a -> {
                    if(a.getName().equals(current_artist.name()) && a.getInstagramUrl().equals(current_artist.instagram_url())) {
                        ArtistProject artistProject = new ArtistProject(a,project);
                        jpaArtistProjectService.create(artistProject);
                        a.getArtistProjects().add(artistProject);
                        project.getArtistProjects().add(artistProject);
                        jpaArtistService.update(a,a.getId());
                    }
                });
            }
        }

        jpaProjectService.update(project,project.getId());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody ProjectImportDto projectImportDto, @PathVariable UUID id) {
        Project project = jpaProjectService.findById(id).orElseThrow();
        Image image = jpaImageService.findById(project.getImage().getId()).orElseThrow();
        image.setData(Base64.getDecoder().decode(projectImportDto.image().data()));
        image.setMimetype(projectImportDto.image().mimetype());
        jpaImageService.update(image,image.getId());
        Location location = jpaLocationService.findById(project.getLocation().getId()).orElseThrow();
        location.setName(projectImportDto.location().name());
        location.setMapAddress(projectImportDto.location().map_address());
        jpaLocationService.update(location,location.getId());
        project.setLocation(location);
        project.setName(projectImportDto.name());
        project.setImage(image);
        project.setYoutubeUrl(projectImportDto.youtube_url());
        jpaProjectService.update(project,id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable String id) {
        jpaProjectService.delete(UUID.fromString(id));
    }

    @GetMapping("/count")
    int count() { return jpaProjectService.count(); }

    @GetMapping("/name/{name}")
    List<ProjectExportDto> findByName(@PathVariable String name) {
        return jpaProjectService.findByName(name).stream().map(this::exportProject).toList();
    }

    private ProjectExportDto exportProject(Project project) {
        ImageDto imageDto = new ImageDto(
                project.getImage().getMimetype(),
                Base64.getEncoder().encodeToString(project.getImage().getData())
        );

        LocationDto locationDto = new LocationDto(
                project.getLocation().getName(),
                project.getLocation().getMapAddress()
        );

        List<ProjectExportDto.ArtistData> artistDataList = project.getArtistProjects().stream()
                .map(ap -> new ProjectExportDto.ArtistData(
                        ap.getArtist().getName(),
                        new ImageDto(
                                ap.getArtist().getImage().getMimetype(),
                                Base64.getEncoder().encodeToString(ap.getArtist().getImage().getData())
                        ),
                        ap.getArtist().getDescription(),
                        ap.getArtist().getInstagramUrl()
                )).toList();

        return new ProjectExportDto(
                project.getName(),
                locationDto,
                imageDto,
                project.getYoutubeUrl(),
                project.getCreatedOn(),
                artistDataList
        );
    }
}
