package com.trails_art.trails.repositories.project;

import com.trails_art.trails.models.Project;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcProjectRepository implements ProjectRepository {
    private final JdbcClient jdbcClient;

    public JdbcProjectRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Project> findAll() {
        return jdbcClient.sql("SELECT * FROM project")
                .query(Project.class)
                .list();
    }

    public Optional<Project> findById(UUID id) {
        return jdbcClient.sql("SELECT id,name,image_id,location_id,youtube_url,created_on FROM project WHERE id = :id" )
                .param(id)
                .query(Project.class)
                .optional();
    }

    public void create(Project project) {
        var updated = jdbcClient.sql("INSERT INTO project(id,name,image_id,location_id,youtube_url,created_on) VALUES(?,?,?,?,?,?)")
                .params(List.of(project.id(), project.name(), project.image(), project.location(), project.youtubeUrl(), project.created_on()))
                .update();

        Assert.state(updated == 1, "Failed to create project " + project.name());
    }

    public void update(Project project, UUID id) {
        var updated = jdbcClient.sql("UPDATE project SET name = ?,image_id = ?,location_id = ?, youtube_url = ?, created_on = ? WHERE id = ?")
                .params(List.of(project.name(), project.image(), project.location(), project.youtubeUrl(), project.created_on(), id))
                .update();

        Assert.state(updated == 1, "Failed to update project " + project.name());
    }

    public void delete(UUID id) {
        var updated = jdbcClient.sql("DELETE FROM project WHERE id = :id")
                .param(id)
                .update();

        Assert.state(updated == 1, "Failed to delete project " + id);
    }

    public int count() {
        return jdbcClient.sql("SELECT * FROM project").query().listOfRows().size();
    }

    public void saveAll(List<Project> projects) {
        projects.forEach(this::create);
    }

    public List<Project> findByName(String name) {
        return jdbcClient.sql("SELECT * FROM project WHERE name = :name")
                .param(name)
                .query(Project.class)
                .list();
    }
}
