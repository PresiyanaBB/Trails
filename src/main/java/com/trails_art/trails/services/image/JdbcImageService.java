package com.trails_art.trails.services.image;

import com.trails_art.trails.models.Image;
import com.trails_art.trails.repositories.image.JdbcImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JdbcImageService implements ImageService {

    private final JdbcImageRepository jdbcImageRepository;

    public JdbcImageService(JdbcImageRepository jdbcImageRepository) {
        this.jdbcImageRepository = jdbcImageRepository;
    }

    public List<Image> findAll() {
        return jdbcImageRepository.findAll();
    }

    public Optional<Image> findById(UUID id) {
        return jdbcImageRepository.findById(id);
    }

    public void create(Image image) {
        jdbcImageRepository.create(image);
    }

    public void update(Image image, UUID id){
        jdbcImageRepository.update(image, id);
    }

    public void delete(UUID id){
        jdbcImageRepository.delete(id);
    }

    public int count(){
        return jdbcImageRepository.count();
    }

    public void saveAll(List<Image> images){
        jdbcImageRepository.saveAll(images);
    }

}
