package com.trails_art.trails.services.location;

import com.trails_art.trails.models.Location;
import com.trails_art.trails.repositories.location.JdbcLocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JdbcLocationService implements LocationService {

    private final JdbcLocationRepository jdbcLocationRepository;

    public JdbcLocationService(JdbcLocationRepository jdbcLocationRepository) {
        this.jdbcLocationRepository = jdbcLocationRepository;
    }

    public List<Location> findAll() {
        return jdbcLocationRepository.findAll();
    }

    public Optional<Location> findById(UUID id) {
        return jdbcLocationRepository.findById(id);
    }

    public void create(Location location) {
        jdbcLocationRepository.create(location);
    }

    public void update(Location location, UUID id){
        jdbcLocationRepository.update(location, id);
    }

    public void delete(UUID id){
        jdbcLocationRepository.delete(id);
    }

    public int count(){
        return jdbcLocationRepository.count();
    }

    public void saveAll(List<Location> locations){
        jdbcLocationRepository.saveAll(locations);
    }

    public List<Location> findByName(String name){
        return jdbcLocationRepository.findByName(name);
    }

    public List<Location> findByMapAddress(String mapAddress) { return jdbcLocationRepository.findByMapAddress(mapAddress); }
}
