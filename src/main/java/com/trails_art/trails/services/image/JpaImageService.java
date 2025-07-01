package com.trails_art.trails.services.image;

import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.repositories.JpaImageRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JpaImageService implements ImageService {

    private final JpaImageRepository jpaImageRepository;

    public JpaImageService(JpaImageRepository jpaImageRepository) {
        this.jpaImageRepository = jpaImageRepository;
    }

    @Override
    public List<Image> findAll() {
        return jpaImageRepository.findAll();
    }

    @Override
    public Optional<Image> findById(UUID id) {
        return jpaImageRepository.findById(id);
    }

    @Override
    public void create(Image image) {
        jpaImageRepository.save(image);
    }

    @Override
    public void createFromDto(ImageDto imageDto) {
        Image image = new Image(imageDto.mimetype(), Base64.getDecoder().decode(imageDto.data()));
        create(image);
    }

    @Override
    public void update(Image image, UUID id) {
        if (jpaImageRepository.existsById(id)) {
            image.setId(id);
            jpaImageRepository.save(image);
        } else {
            throw new InvalidArgumentIdException("Image with ID " + id + " not found.");
        }
    }

    @Override
    public void updateFromDto(ImageDto imageDto, UUID id) {
        Image image = findById(id).orElseThrow();
        image.setData(Base64.getDecoder().decode(imageDto.data()));
        image.setMimetype(imageDto.mimetype());
        update(image,id);
    }

    @Override
    public void delete(UUID id) {
        jpaImageRepository.deleteById(id);
    }

    @Override
    public int count() {
        return (int) jpaImageRepository.count();
    }

    @Override
    public void saveAll(List<Image> images) {
        jpaImageRepository.saveAll(images);
    }
}
