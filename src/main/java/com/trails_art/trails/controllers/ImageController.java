package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.services.image.JpaImageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final JpaImageService jpaImageService;

    ImageController(JpaImageService jpaImageService) {
        this.jpaImageService = jpaImageService;
    }

    @GetMapping
    List<Image> findAll() {
        return jpaImageService.findAll();
    }

    @GetMapping("/{id}")
    Image findById(@PathVariable UUID id) {
        Optional<Image> image = jpaImageService.findById(id);
        if(image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found.");
        }
        return image.get();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void create(@Valid @RequestBody ImageDto imageDto) {
        Image image = new Image(imageDto.mimetype(), Base64.getDecoder().decode(imageDto.data()));
        jpaImageService.create(image);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void update(@Valid @RequestBody ImageDto imageDto, @PathVariable UUID id) {
        Image image = jpaImageService.findById(id).orElseThrow();
        image.setData(Base64.getDecoder().decode(imageDto.data()));
        image.setMimetype(imageDto.mimetype());
        jpaImageService.update(image,id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable String id) {
        jpaImageService.delete(UUID.fromString(id));
    }

    @GetMapping("/count")
    int count() { return jpaImageService.count(); }
}
