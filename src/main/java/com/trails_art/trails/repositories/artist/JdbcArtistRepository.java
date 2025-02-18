package com.trails_art.trails.repositories.artist;

import com.trails_art.trails.modules.Artist;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcArtistRepository implements ArtistRepository {
    private final JdbcClient jdbcClient;

    public JdbcArtistRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Artist> findAll() {
        return jdbcClient.sql("SELECT * FROM artist")
                .query(Artist.class)
                .list();
    }

    public Optional<Artist> findById(UUID id) {
        return jdbcClient.sql("SELECT id,name,image_id,description,instagram_url FROM artist WHERE id = :id" )
                .param(id)
                .query(Artist.class)
                .optional();
    }

    public void create(Artist artist) {
        var updated = jdbcClient.sql("INSERT INTO artist(id,name,image_id,description,instagram_url) VALUES(?,?,?,?,?)")
                .params(List.of(artist.id(), artist.name(), artist.image(), artist.description(), artist.instagramUrl()))
                .update();

        Assert.state(updated == 1, "Failed to create artist " + artist.name());
    }

    public void update(Artist artist, UUID id) {
        var updated = jdbcClient.sql("UPDATE artist SET name = ?,image_id = ?,description = ?,instagram_url = ? WHERE id = ?")
                .params(List.of(artist.name(), artist.image(), artist.description(), artist.instagramUrl(), id))
                .update();

        Assert.state(updated == 1, "Failed to update artist " + artist.name());
    }

    public void delete(UUID id) {
        var updated = jdbcClient.sql("DELETE FROM artist WHERE id = :id")
                .param(id)
                .update();

        Assert.state(updated == 1, "Failed to delete artist " + id);
    }

    public int count() {
        return jdbcClient.sql("SELECT * FROM artist").query().listOfRows().size();
    }

    public void saveAll(List<Artist> artists) {
        artists.forEach(this::create);
    }

    public List<Artist> findByName(String name) {
        return jdbcClient.sql("SELECT * FROM artist WHERE name = :name")
                .param(name)
                .query(Artist.class)
                .list();
    }
}
