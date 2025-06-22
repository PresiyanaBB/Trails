package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.services.image.ImageService;
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

    private final ImageService imageService;

    ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    List<Image> findAll() {
        return imageService.findAll();
    }

    @GetMapping("/{id}")
    Image findById(@PathVariable UUID id) {
        Optional<Image> image = imageService.findById(id);
        if(image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found.");
        }
        return image.get();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody ImageDto imageDto) {
        imageService.createFromDto(imageDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody ImageDto imageDto, @PathVariable UUID id) {
        imageService.updateFromDto(imageDto,id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable String id) {
        imageService.delete(UUID.fromString(id));
    }

    @GetMapping("/count")
    int count() { return imageService.count(); }
}
