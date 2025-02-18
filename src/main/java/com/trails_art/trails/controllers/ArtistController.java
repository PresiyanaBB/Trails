package com.trails_art.trails.controllers;

import com.trails_art.trails.models.Artist;
import com.trails_art.trails.services.artist.JdbcArtistService;
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

    private final JdbcArtistService jdbcArtistService;

    ArtistController(JdbcArtistService jdbcArtistService) {
        this.jdbcArtistService = jdbcArtistService;
    }

    @GetMapping
    List<Artist> findAll() {
        return jdbcArtistService.findAll();
    }

    @GetMapping("/{id}")
    Artist findById(@PathVariable UUID id) {
        Optional<Artist> artist = jdbcArtistService.findById(id);
        if(artist.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found.");
        }
        return artist.get();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody Artist artist) {
        jdbcArtistService.create(artist);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody Artist artist, @PathVariable UUID id) {
        jdbcArtistService.update(artist,id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable UUID id) {
        jdbcArtistService.delete(id);
    }

    @GetMapping("/count")
    int count() { return jdbcArtistService.count(); }

    @GetMapping("/{name}")
    List<Artist> findByName(@PathVariable String name) {
        return jdbcArtistService.findByName(name);
    }
}
