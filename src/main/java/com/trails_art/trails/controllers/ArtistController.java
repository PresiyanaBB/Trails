package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.ArtistImportDto;
import com.trails_art.trails.dtos.ArtistExportDto;
import com.trails_art.trails.dtos.export.ExportDtoMethods;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.services.artist.ArtistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/artists")
class ArtistController {

    private final ArtistService artistService;

    ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    List<ArtistExportDto> findAll() {
        return artistService.findAll().stream().map(ExportDtoMethods::exportArtist).toList();
    }

    @GetMapping("/{id}")
    ArtistExportDto findById(@PathVariable UUID id) {
        Optional<Artist> artist = artistService.findById(id);
        if (artist.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found.");
        }
        return ExportDtoMethods.exportArtist(artist.get());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody ArtistImportDto artistImportDto) {
        artistService.createFromDto(artistImportDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody ArtistImportDto artistImportDto, @PathVariable UUID id) {
        artistService.updateFromDto(artistImportDto,id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable String id) {
        artistService.delete(UUID.fromString(id));
    }

    @GetMapping("/count")
    int count() {
        return artistService.count();
    }

    @GetMapping("/name/{name}")
    List<ArtistExportDto> findByName(@PathVariable String name) {
        return artistService.findByName(name).stream().map(ExportDtoMethods::exportArtist).toList();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/add-projects/{id}")
    void addProjects(@PathVariable UUID id, @RequestBody List<String> projects) {
        List<UUID> projectIds = projects.stream()
                .map(UUID::fromString)
                .toList();
        artistService.addProjects(projectIds, id);
    }
}
