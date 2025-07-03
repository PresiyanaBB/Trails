package com.trails_art.trails.repositories;

import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.repositories.artist.JpaArtistRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.nio.charset.StandardCharsets;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ArtistRepositoryTest {
    @Autowired
    private JpaArtistRepository artistRepository;

    @Test
    public void ArtistRepository_findByNameContainingIgnoreCase() {
        byte[] imageData = "test-image".getBytes(StandardCharsets.UTF_8);
        Image image = new Image("image/jpg",imageData );
        Artist artist = new Artist("Test", image , "description", "https://instagram.com/test");

        artistRepository.save(artist);

        Assertions.assertEquals(1,artistRepository.findByNameContainingIgnoreCase("tESt").size());
    }
}
