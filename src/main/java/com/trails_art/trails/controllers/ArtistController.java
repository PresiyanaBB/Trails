package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.ArtistImportDto;
import com.trails_art.trails.dtos.ArtistExportDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.mappers.ArtistMapper;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.services.artist.ArtistService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public ResponseEntity<List<ArtistExportDto>> findAll() {
        List<ArtistExportDto> dtos = artistService.findAll().stream()
                .map(ArtistMapper::mapToArtistDto)
                .toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(dtos.size()))
                .body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistExportDto> findById(@PathVariable UUID id) {
        Artist artist = artistService.findById(id)
                .orElseThrow(() -> new InvalidArgumentIdException("Artist not found."));
        return ResponseEntity.ok(ArtistMapper.mapToArtistDto(artist));
    }

    @GetMapping(params = "name")
    public ResponseEntity<List<ArtistExportDto>> findByName(@RequestParam String name) {
        List<ArtistExportDto> dtos = artistService.findByName(name).stream()
                .map(ArtistMapper::mapToArtistDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ArtistExportDto> create(@Valid @RequestBody ArtistImportDto dto) {
        Artist artist = artistService.createFromDto(dto);
        URI location = URI.create("/api/artists/" + artist.getId());
        return ResponseEntity.created(location)
                .body(ArtistMapper.mapToArtistDto(artist));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ArtistExportDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody ArtistImportDto dto
    ) {
        Artist artist = artistService.updateFromDto(dto, id);
        return ResponseEntity.ok(ArtistMapper.mapToArtistDto(artist));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        artistService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/projects")
    @Transactional
    public ResponseEntity<List<UUID>> addProjects(
            @PathVariable UUID id,
            @RequestBody List<String> projectIdsRaw
    ) {
        List<UUID> projectIds = projectIdsRaw.stream()
                .map(UUID::fromString)
                .toList();
        artistService.addProjects(projectIds, id);
        return ResponseEntity.ok(projectIds);
    }
}
