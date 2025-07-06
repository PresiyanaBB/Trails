package com.trails_art.trails.services.location;

import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.mappers.LocationMapper;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.repositories.JpaLocationRepository;
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
    public Location createFromDto(LocationDto locationDto) {
        Location location = LocationMapper.mapToLocation(locationDto);
        create(location);
        return location;
    }

    @Override
    public void update(Location location, UUID id) {
        if (jpaLocationRepository.existsById(id)) {
            location.setId(id);
            jpaLocationRepository.save(location);
        } else {
            throw new InvalidArgumentIdException("Location with ID " + id + " not found.");
        }
    }

    @Override
    public Location updateFromDto(LocationDto locationDto, UUID id) {
        Location location = findById(id).orElseThrow(() -> new InvalidArgumentIdException("Location with ID " + id + " not found."));
        location.setName(locationDto.name());
        location.setMapAddress(locationDto.map_address());
        update(location,id);
        return location;
    }

    @Override
    public void delete(UUID id) {
        jpaLocationRepository.findById(id).orElseThrow(() -> new InvalidArgumentIdException("Location with ID " + id + " not found."));
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