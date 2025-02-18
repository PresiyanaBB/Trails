package com.trails_art.trails.repositories.image;

import com.trails_art.trails.models.Image;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImageRepository {
    List<Image> findAll();

    Optional<Image> findById(UUID id);

    void create(Image image);

    void update(Image image, UUID id);

    void delete(UUID id);

    int count();

    void saveAll(List<Image> images);
}
