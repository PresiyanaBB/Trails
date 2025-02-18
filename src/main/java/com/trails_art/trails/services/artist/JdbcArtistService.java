package com.trails_art.trails.services.artist;

import com.trails_art.trails.models.Artist;
import com.trails_art.trails.repositories.artist.JdbcArtistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JdbcArtistService implements ArtistService {

    private final JdbcArtistRepository jdbcArtistRepository;

    public JdbcArtistService(JdbcArtistRepository jdbcArtistRepository) {
        this.jdbcArtistRepository = jdbcArtistRepository;
    }

    public List<Artist> findAll() {
        return jdbcArtistRepository.findAll();
    }

    public Optional<Artist> findById(UUID id) {
        return jdbcArtistRepository.findById(id);
    }

    public void create(Artist artist) {
        jdbcArtistRepository.create(artist);
    }

    public void update(Artist artist, UUID id){
        jdbcArtistRepository.update(artist, id);
    }

    public void delete(UUID id){
        jdbcArtistRepository.delete(id);
    }

    public int count(){
        return jdbcArtistRepository.count();
    }

    public void saveAll(List<Artist> artists){
        jdbcArtistRepository.saveAll(artists);
    }

    public List<Artist> findByName(String name){
        return jdbcArtistRepository.findByName(name);
    }
}
