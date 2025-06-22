package com.trails_art.trails.services.image;

import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.models.Image;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImageService {
    List<Image> findAll();

    Optional<Image> findById(UUID id);

    void create(Image image);

    void createFromDto(ImageDto imageDto);

    void update(Image image, UUID id);

    void updateFromDto(ImageDto imageDto, UUID id);

    void delete(UUID id);

    int count();

    void saveAll(List<Image> images);
}
