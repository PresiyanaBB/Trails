package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.ArtistImportDto;
import com.trails_art.trails.dtos.ArtistExportDto;
import com.trails_art.trails.dtos.export.ExportDtoMethods;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.exceptions.InvalidDTOFormat;
import com.trails_art.trails.services.artist.ArtistService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
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
    ResponseEntity<List<ArtistExportDto>> findAll() {
        return new ResponseEntity<>(artistService.findAll().stream().map(ExportDtoMethods::exportArtist)
                .toList(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        try {
            Optional<ArtistExportDto> artist = artistService.findById(id)
                    .map(ExportDtoMethods::exportArtist);

            if (artist.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Artist not found."));
            }

            return ResponseEntity.ok(artist.get());
        } catch (InvalidArgumentIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }


    @Transactional
    @PostMapping
    ResponseEntity<?> create(@Valid @RequestBody ArtistImportDto artistImportDto) {
        try {
            artistService.createFromDto(artistImportDto);
        } catch (InvalidDTOFormat e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Transactional
    @PutMapping("/{id}")
    ResponseEntity<?> update(@Valid @RequestBody ArtistImportDto artistImportDto, @PathVariable UUID id) {
        try {
            artistService.updateFromDto(artistImportDto, id);
        } catch (InvalidArgumentIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (InvalidDTOFormat e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @Transactional
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable String id) {
       try {
           artistService.delete(UUID.fromString(id));
       } catch (InvalidArgumentIdException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
       }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/count")
    ResponseEntity<Integer> count() {
        return ResponseEntity.ok(artistService.count());
    }

    @GetMapping("/name/{name}")
    ResponseEntity<?> findByName(@PathVariable String name) {
        List<ArtistExportDto> artists;
        try {
            artists  = artistService.findByName(name).stream().map(ExportDtoMethods::exportArtist).toList();
        } catch (InvalidArgumentIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }

        return ResponseEntity.ok(artists);
    }

    @Transactional
    @PutMapping("/add-projects/{id}")
    ResponseEntity<?> addProjects(@PathVariable UUID id, @RequestBody List<String> projects) {
        List<UUID> projectIds = projects.stream()
                .map(UUID::fromString)
                .toList();

        try {
            artistService.addProjects(projectIds, id);
        } catch (InvalidArgumentIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (InvalidDTOFormat e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
