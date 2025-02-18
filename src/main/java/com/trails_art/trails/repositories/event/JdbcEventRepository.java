package com.trails_art.trails.repositories.event;

import com.trails_art.trails.models.Event;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcEventRepository implements EventRepository {

    private final JdbcClient jdbcClient;

    public JdbcEventRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Event> findAll() {
        return jdbcClient.sql("SELECT * FROM event")
                .query(Event.class)
                .list();
    }

    public Optional<Event> findById(UUID id) {
        return jdbcClient.sql("SELECT id,name,image_id,description,location_id,start_time,end_time FROM event WHERE id = :id" )
                .param(id)
                .query(Event.class)
                .optional();
    }

    public void create(Event event) {
        var updated = jdbcClient.sql("INSERT INTO event(id,name,image_id,description,location_id,start_time,end_time) VALUES(?,?,?,?,?,?,?)")
                .params(List.of(event.id(), event.name(), event.image(), event.description(), event.location(), event.start_time(),event.end_time()))
                .update();

        Assert.state(updated == 1, "Failed to create event " + event.name());
    }

    public void update(Event event, UUID id) {
        var updated = jdbcClient.sql("UPDATE event SET name = ?,image_id = ?,description = ?,location_id = ?,start_time = ?, end_time = ? WHERE id = ?")
                .params(List.of(event.name(), event.image(), event.description(), event.location(), event.start_time(),event.end_time(), id))
                .update();

        Assert.state(updated == 1, "Failed to update event " + event.name());
    }

    public void delete(UUID id) {
        var updated = jdbcClient.sql("DELETE FROM event WHERE id = :id")
                .param(id)
                .update();

        Assert.state(updated == 1, "Failed to delete event " + id);
    }

    public int count() {
        return jdbcClient.sql("SELECT * FROM event").query().listOfRows().size();
    }

    public void saveAll(List<Event> events) {
        events.forEach(this::create);
    }

    public List<Event> findByName(String name) {
        return jdbcClient.sql("SELECT * FROM event WHERE name = :name")
                .param(name)
                .query(Event.class)
                .list();
    }
}
