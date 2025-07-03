package com.trails_art.trails.repositories;

import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.models.Project;
import com.trails_art.trails.repositories.project.JpaProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.nio.charset.StandardCharsets;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ProjectRepositoryTest {
    @Autowired
    private JpaProjectRepository projectRepository;

    @Test
    @DisplayName("findByNameContainingIgnoreCase: finds project by name case-insensitive")
    public void findByNameContainingIgnoreCase() {
        byte[] imageData = "test-image".getBytes(StandardCharsets.UTF_8);
        Image image = new Image("image/jpg",imageData );
        Location location = new Location("name","address");
        Project project = new Project("Test",location,image,"https://youtube.com/test");

        projectRepository.save(project);

        Assertions.assertEquals(1,projectRepository.findByNameContainingIgnoreCase("tESt").size());
    }
}
