package com.trails_art.trails.clients;

import com.trails_art.trails.models.Artist;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.UUID;

@RequestMapping()
public interface ArtistHttpClient {
    @GetMapping()
    List<Artist> findAll();

    @GetMapping("/{id}")
    Artist findById(@PathVariable UUID id);

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    void create(@Valid @RequestBody Artist artist);

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody Artist artist,@PathVariable UUID id);

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable UUID id);

    @GetMapping("/count")
    int count();

    @GetMapping("/{name}")
    List<Artist> findByName(@PathVariable String name);
}
