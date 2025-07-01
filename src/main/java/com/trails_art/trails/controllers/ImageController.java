package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.dtos.export.ExportDtoMethods;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.exceptions.InvalidDTOFormat;
import com.trails_art.trails.services.image.ImageService;
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
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    ResponseEntity<List<ImageDto>> findAll() {
        return new ResponseEntity<>(imageService.findAll().stream().map(ExportDtoMethods::exportImage).toList(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<ImageDto> findById(@PathVariable UUID id) {
        Optional<ImageDto> image;
        try {
            image = imageService.findById(id).map(ExportDtoMethods::exportImage);
        } catch (InvalidArgumentIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        if(image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found.");
        }
        return new ResponseEntity<>(image.get(), HttpStatus.OK);
    }

    @Transactional
    @PostMapping
    ResponseEntity<Void> create(@Valid @RequestBody ImageDto imageDto) {
        try {
            imageService.createFromDto(imageDto);
        } catch (InvalidDTOFormat e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Transactional
    @PutMapping("/{id}")
    ResponseEntity<Void> update(@Valid @RequestBody ImageDto imageDto, @PathVariable UUID id) {
        try {
            imageService.updateFromDto(imageDto,id);
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
            imageService.delete(UUID.fromString(id));
        } catch (InvalidArgumentIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/count")
    ResponseEntity<Integer> count() { return new ResponseEntity<>(imageService.count(), HttpStatus.OK); }
}
