package com.trails_art.trails.services.location;

import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.repositories.location.JpaLocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JpaLocationService implements LocationService {

    private final JpaLocationRepository jpaLocationRepository;

    public JpaLocationService(JpaLocationRepository jpaLocationRepository) {
        this.jpaLocationRepository = jpaLocationRepository;
    }

    @Override
    public List<Location> findAll() {
        return jpaLocationRepository.findAll();
    }

    @Override
    public Optional<Location> findById(UUID id) {
        return jpaLocationRepository.findById(id);
    }

    @Override
    public void create(Location location) {
        jpaLocationRepository.save(location);
    }

    @Override
    public void createFromDto(LocationDto locationDto) {
        Location location = new Location(locationDto.name(), locationDto.map_address());
        create(location);
    }

    @Override
    public void update(Location location, UUID id) {
        if (jpaLocationRepository.existsById(id)) {
            location.setId(id);
            jpaLocationRepository.save(location);
        } else {
            throw new IllegalArgumentException("Location with ID " + id + " not found.");
        }
    }

    @Override
    public void updateFromDto(LocationDto locationDto, UUID id) {
        Location location = findById(id).orElseThrow();
        location.setName(locationDto.name());
        location.setMapAddress(locationDto.map_address());
        update(location,id);
    }

    @Override
    public void delete(UUID id) {
        jpaLocationRepository.deleteById(id);
    }

    @Override
    public int count() {
        return (int) jpaLocationRepository.count();
    }

    @Override
    public void saveAll(List<Location> locations) {
        jpaLocationRepository.saveAll(locations);
    }

    @Override
    public List<Location> findByName(String name) {
        return jpaLocationRepository.findByNameContainingIgnoreCase(name);
    }
}