package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.ArtistDto;
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
    void create(@Valid @RequestBody ArtistDto artistDto) {
        Image image_project = new Image(artistDto.projects()[0].image().mimetype(), Base64.getDecoder().decode(artistDto.projects()[0].image().data()));
        Image image_artist = new Image(artistDto.image().mimetype(),Base64.getDecoder().decode(artistDto.image().data()));
        Artist artist = new Artist(artistDto.name(),image_artist, artistDto.description(), artistDto.instagram_url());
        Location location = new Location(artistDto.projects()[0].location().name(),artistDto.projects()[0].location().map_address());
        Project project = new Project(artistDto.projects()[0].name(),location,image_project,artistDto.projects()[0].youtube_url());
        ArtistProject artistProject = new ArtistProject(artist,project);
        jpaImageService.create(image_artist);
        jpaImageService.create(image_project);
        jpaLocationService.create(location);
        jpaArtistService.create(artist);
        jpaProjectService.create(project);
        jpaArtistProjectService.create(artistProject);
        artist.getArtistProjects().add(artistProject);
        project.getArtistProjects().add(artistProject);
        jpaArtistService.update(artist,artist.getId());
        jpaProjectService.update(project,project.getId());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody ArtistDto artistDto, @PathVariable UUID id) {
        Artist artist = jpaArtistService.findById(id).orElseThrow();
        artist.setName(artistDto.name());
        artist.setDescription(artistDto.description());
        artist.setInstagramUrl(artistDto.instagram_url());
        Image image = jpaImageService.findById(artist.getImage().getId()).orElseThrow();
        image.setMimetype(artistDto.image().mimetype());
        image.setData(Base64.getDecoder().decode(artistDto.image().data()));
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
