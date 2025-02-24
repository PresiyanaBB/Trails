package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.ArtistImportDto;
import com.trails_art.trails.dtos.ArtistExportDto;
import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.dtos.LocationDto;
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
@RequestMapping("/api/artists")
class ArtistController {

    private final JpaProjectService jpaProjectService;
    private final JpaArtistService jpaArtistService;
    private final JpaImageService jpaImageService;
    private final JpaLocationService jpaLocationService;
    private final JpaArtistProjectService jpaArtistProjectService;

    ArtistController(JpaArtistService jpaArtistService, JpaProjectService jpaProjectService,
                     JpaImageService jpaImageService, JpaLocationService jpaLocationService,
                     JpaArtistProjectService jpaArtistProjectService) {
        this.jpaArtistService = jpaArtistService;
        this.jpaProjectService = jpaProjectService;
        this.jpaImageService = jpaImageService;
        this.jpaLocationService = jpaLocationService;
        this.jpaArtistProjectService = jpaArtistProjectService;
    }

    @GetMapping
    List<ArtistExportDto> findAll() {
        return jpaArtistService.findAll().stream().map(this::exportArtist).toList();
    }

    @GetMapping("/{id}")
    ArtistExportDto findById(@PathVariable UUID id) {
        Optional<Artist> artist = jpaArtistService.findById(id);
        if(artist.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found.");
        }
        return exportArtist(artist.get());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody ArtistImportDto artistImportDto) {
        Image image_artist = new Image(artistImportDto.image().mimetype(),Base64.getDecoder().decode(artistImportDto.image().data()));
        Artist artist = new Artist(artistImportDto.name(),image_artist, artistImportDto.description(), artistImportDto.instagram_url());
        jpaArtistService.create(artist);
        jpaImageService.create(image_artist);

        int size = artistImportDto.projects().size();
        for (int i = 0; i < size; i++) {
            ArtistImportDto.ProjectData current_project = artistImportDto.projects().get(i);
            if(!artistImportDto.is_project_existing().get(i)) {
                Image image_project = new Image(current_project.image().mimetype(), Base64.getDecoder().decode(current_project.image().data()));
                Location location = new Location(current_project.location().name(), current_project.location().map_address());
                Project project = new Project(current_project.name(),location,image_project, current_project.youtube_url());
                ArtistProject artistProject = new ArtistProject(artist,project);
                jpaImageService.create(image_project);
                jpaLocationService.create(location);
                jpaProjectService.create(project);
                jpaArtistProjectService.create(artistProject);
                artist.getArtistProjects().add(artistProject);
                project.getArtistProjects().add(artistProject);
                jpaProjectService.update(project,project.getId());
            }
            else {
                List<Project> projects = jpaProjectService.findByName(current_project.name());
                projects.forEach(pr -> {
                    if(pr.getLocation().getName().equalsIgnoreCase(current_project.location().name()) &&
                            pr.getLocation().getMapAddress().equalsIgnoreCase(current_project.location().map_address())) {
                        ArtistProject artistProject = new ArtistProject(artist,pr);
                        jpaArtistProjectService.create(artistProject);
                        artist.getArtistProjects().add(artistProject);
                        pr.getArtistProjects().add(artistProject);
                        jpaProjectService.update(pr,pr.getId());
                    }
                });
            }
        }
        jpaArtistService.update(artist,artist.getId());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody ArtistImportDto artistImportDto, @PathVariable UUID id) {
        Artist artist = jpaArtistService.findById(id).orElseThrow();
        artist.setName(artistImportDto.name());
        artist.setDescription(artistImportDto.description());
        artist.setInstagramUrl(artistImportDto.instagram_url());
        Image image = jpaImageService.findById(artist.getImage().getId()).orElseThrow();
        image.setMimetype(artistImportDto.image().mimetype());
        image.setData(Base64.getDecoder().decode(artistImportDto.image().data()));
        artist.setImage(image);
        jpaArtistService.update(artist,id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable String id) {
        jpaArtistService.delete(UUID.fromString(id));
    }

    @GetMapping("/count")
    int count() { return jpaArtistService.count(); }

    @GetMapping("/name/{name}")
    List<ArtistExportDto> findByName(@PathVariable String name) {
        return jpaArtistService.findByName(name).stream().map(this::exportArtist).toList();
    }

    private ArtistExportDto exportArtist(Artist artist) {
        ImageDto imageDto = new ImageDto(
                artist.getImage().getMimetype(),
                Base64.getEncoder().encodeToString(artist.getImage().getData())
        );

        List<ArtistExportDto.ProjectData> projectDataList = artist.getArtistProjects().stream()
                .map(ap -> new ArtistExportDto.ProjectData(
                        ap.getProject().getName(),
                        new LocationDto(
                                ap.getProject().getLocation().getName(),
                                ap.getProject().getLocation().getMapAddress()
                        ),
                        new ImageDto(
                                ap.getProject().getImage().getMimetype(),
                                Base64.getEncoder().encodeToString(ap.getProject().getImage().getData())
                        ),
                        ap.getProject().getYoutubeUrl(),
                        ap.getProject().getCreatedOn().toString()
                )).toList();

        return new ArtistExportDto(
                artist.getName(),
                imageDto,
                artist.getDescription(),
                artist.getInstagramUrl(),
                projectDataList
        );
    }
}
