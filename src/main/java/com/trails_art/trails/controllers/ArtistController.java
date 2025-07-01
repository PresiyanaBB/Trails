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
    ResponseEntity<ArtistExportDto> findById(@PathVariable UUID id) {
        Optional<ArtistExportDto> artist;
        try {
            artist = artistService.findById(id).map(ExportDtoMethods::exportArtist);
        } catch (InvalidArgumentIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        if (artist.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found.");
        }

        return new ResponseEntity<>(artist.get(), HttpStatus.OK);
    }

    @Transactional
    @PostMapping
    ResponseEntity<Void> create(@Valid @RequestBody ArtistImportDto artistImportDto) {
        try {
            artistService.createFromDto(artistImportDto);
        } catch (InvalidDTOFormat e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Transactional
    @PutMapping("/{id}")
    ResponseEntity<Void> update(@Valid @RequestBody ArtistImportDto artistImportDto, @PathVariable UUID id) {
        try {
            artistService.updateFromDto(artistImportDto,id);
        } catch (InvalidArgumentIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InvalidDTOFormat e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @Transactional
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable String id) {
       try {
           artistService.delete(UUID.fromString(id));
       } catch (InvalidArgumentIdException e) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
       } catch (Exception e) {
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
       }

       return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping("/count")
    ResponseEntity<Integer> count() {
        return new ResponseEntity<>(artistService.count(), HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    ResponseEntity<List<ArtistExportDto>> findByName(@PathVariable String name) {
        List<ArtistExportDto> artists;
        try {
            artists  = artistService.findByName(name).stream().map(ExportDtoMethods::exportArtist).toList();
        } catch (IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(artists, HttpStatus.OK);
    }

    @Transactional
    @PutMapping("/add-projects/{id}")
    ResponseEntity<Void> addProjects(@PathVariable UUID id, @RequestBody List<String> projects) {
        List<UUID> projectIds = projects.stream()
                .map(UUID::fromString)
                .toList();

        try {
            artistService.addProjects(projectIds, id);
        } catch (InvalidArgumentIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InvalidDTOFormat e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
