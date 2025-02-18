package com.trails_art.trails.repositories.location;

import com.trails_art.trails.models.Location;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcLocationRepository implements LocationRepository {
    private final JdbcClient jdbcClient;

    public JdbcLocationRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Location> findAll() {
        return jdbcClient.sql("SELECT * FROM location")
                .query(Location.class)
                .list();
    }

    public Optional<Location> findById(UUID id) {
        return jdbcClient.sql("SELECT id,name,map_address FROM location WHERE id = :id" )
                .param(id)
                .query(Location.class)
                .optional();
    }

    public void create(Location location) {
        var updated = jdbcClient.sql("INSERT INTO location(id,name,map_address) VALUES(?,?,?)")
                .params(List.of(location.id(), location.name(), location.map_address()))
                .update();

        Assert.state(updated == 1, "Failed to create location " + location.name());
    }

    public void update(Location location, UUID id) {
        var updated = jdbcClient.sql("UPDATE location SET name = ?,map_address = ? WHERE id = ?")
                .params(List.of(location.name(), location.map_address(), id))
                .update();

        Assert.state(updated == 1, "Failed to update location " + location.name());
    }

    public void delete(UUID id) {
        var updated = jdbcClient.sql("DELETE FROM location WHERE id = :id")
                .param(id)
                .update();

        Assert.state(updated == 1, "Failed to delete location " + id);
    }

    public int count() {
        return jdbcClient.sql("SELECT * FROM location").query().listOfRows().size();
    }

    public void saveAll(List<Location> locations) {
        locations.forEach(this::create);
    }

    public List<Location> findByName(String name) {
        return jdbcClient.sql("SELECT * FROM location WHERE name = :name")
                .param(name)
                .query(Location.class)
                .list();
    }

    public List<Location> findByMapAddress(String mapAddress) {
        return jdbcClient.sql("SELECT * FROM location WHERE map_address = :mapAddress")
                .param(mapAddress)
                .query(Location.class)
                .list();
    }
}
