package com.trails_art.trails.controllers;

import com.trails_art.trails.repositories.artist.JdbcArtistRepository;
import com.trails_art.trails.models.Artist;
import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/artists")
class ArtistController {

    private final JdbcArtistRepository artistRepository;

    ArtistController(JdbcArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @GetMapping
    List<Artist> findAll() {
        return artistRepository.findAll();
    }

    @GetMapping("/{id}")
    Artist findById(@PathVariable UUID id) {
        Optional<Artist> artist = artistRepository.findById(id);
        if(artist.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found.");
        }
        return artist.get();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody Artist artist) {
        artistRepository.create(artist);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody Artist artist, @PathVariable UUID id) {
        artistRepository.update(artist,id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable UUID id) {
        artistRepository.delete(id);
    }

    @GetMapping("/{name}")
    List<Artist> findByName(@PathVariable String name) {
        return artistRepository.findByName(name);
    }
}
