package com.trails_art.trails.repositories.image;

import com.trails_art.trails.models.Image;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcImageRepository implements ImageRepository {

    private final JdbcClient jdbcClient;

    public JdbcImageRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Image> findAll() {
        return jdbcClient.sql("SELECT * FROM image")
                .query(Image.class)
                .list();
    }

    public Optional<Image> findById(UUID id) {
        return jdbcClient.sql("SELECT id,mimetype,data FROM image WHERE id = :id" )
                .param(id)
                .query(Image.class)
                .optional();
    }

    public void create(Image image) {
        var updated = jdbcClient.sql("INSERT INTO image(id,mimetype,data) VALUES(?,?,?)")
                .params(List.of(image.id(),image.MIMEType(),image.data()))
                .update();

        Assert.state(updated == 1, "Failed to create image " + image.id());
    }

    public void update(Image image, UUID id) {
        var updated = jdbcClient.sql("UPDATE image SET mimetype = ? , data = ? WHERE id = ?")
                .params(List.of(image.MIMEType(), image.data(), id))
                .update();

        Assert.state(updated == 1, "Failed to update image " + image.id());
    }

    public void delete(UUID id) {
        var updated = jdbcClient.sql("DELETE FROM image WHERE id = :id")
                .param(id)
                .update();

        Assert.state(updated == 1, "Failed to delete image " + id);
    }

    public int count() {
        return jdbcClient.sql("SELECT * FROM image").query().listOfRows().size();
    }

    public void saveAll(List<Image> images) {
        images.forEach(this::create);
    }

}
