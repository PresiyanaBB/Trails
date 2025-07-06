package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.mappers.ImageMapper;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.services.image.ImageService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public ResponseEntity<List<ImageDto>> findAll() {
        List<ImageDto> dtos = imageService.findAll().stream()
                .map(ImageMapper::mapToImageDto)
                .toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(dtos.size()))
                .body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageDto> findById(@PathVariable UUID id) {
        Image img = imageService.findById(id)
                .orElseThrow(() -> new InvalidArgumentIdException("Image not found."));
        return ResponseEntity.ok(ImageMapper.mapToImageDto(img));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ImageDto> create(@Valid @RequestBody ImageDto dto) {
        Image img = imageService.createFromDto(dto);
        URI location = URI.create("/api/images/" + img.getId());
        return ResponseEntity.created(location)
                .body(ImageMapper.mapToImageDto(img));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ImageDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody ImageDto dto
    ) {
        Image img = imageService.updateFromDto(dto, id);
        return ResponseEntity.ok(ImageMapper.mapToImageDto(img));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        imageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
