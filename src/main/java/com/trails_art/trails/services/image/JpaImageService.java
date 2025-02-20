package com.trails_art.trails.services.image;

import com.trails_art.trails.models.Image;
import com.trails_art.trails.repositories.image.JpaImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JpaImageService implements ImageService {

    private final JpaImageRepository jpaImageRepository;

    public JpaImageService(JpaImageRepository jpaImageRepository) {
        this.jpaImageRepository = jpaImageRepository;
    }

    public List<Image> findAll() {
        return jpaImageRepository.findAll();
    }

    public Optional<Image> findById(UUID id) {
        return jpaImageRepository.findById(id);
    }

    public void create(Image image) {
        jpaImageRepository.save(image);
    }

    public void update(Image image, UUID id) {
        if (jpaImageRepository.existsById(id)) {
            image.setId(id);
            jpaImageRepository.save(image);
        } else {
            throw new IllegalArgumentException("Image with ID " + id + " not found.");
        }
    }

    public void delete(UUID id) {
        jpaImageRepository.deleteById(id);
    }

    public int count() {
        return (int) jpaImageRepository.count();
    }

    public void saveAll(List<Image> images) {
        jpaImageRepository.saveAll(images);
    }
}
