package com.trails_art.trails.controllers;

import com.trails_art.trails.models.Image;
import com.trails_art.trails.services.image.JdbcImageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final JdbcImageService jdbcImageService;

    ImageController(JdbcImageService jdbcImageService) {
        this.jdbcImageService = jdbcImageService;
    }

    @GetMapping
    List<Image> findAll() {
        return jdbcImageService.findAll();
    }

    @GetMapping("/{id}")
    Image findById(@PathVariable UUID id) {
        Optional<Image> image = jdbcImageService.findById(id);
        if(image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found.");
        }
        return image.get();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody Image image) {
        jdbcImageService.create(image);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody Image image, @PathVariable UUID id) {
        jdbcImageService.update(image,id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable UUID id) {
        jdbcImageService.delete(id);
    }

    @GetMapping("/count")
    int count() { return jdbcImageService.count(); }
}
