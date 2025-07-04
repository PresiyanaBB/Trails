package com.trails_art.trails.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.repositories.JpaImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class ImageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaImageRepository imageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Image savedImage;
    private Image savedImage2;

    @BeforeEach
    void setUp() {
        imageRepository.deleteAll();
        Image image1 = imageRepository.save(new Image("image/png", "test-image1".getBytes(StandardCharsets.UTF_8)));
        Image image2 = imageRepository.save(new Image("image/png", "test-image2".getBytes(StandardCharsets.UTF_8)));
        savedImage = imageRepository.findAll().get(0);
        savedImage2 = imageRepository.findAll().get(1);
    }

    @Test
    @DisplayName("GET /api/images - returns all images")
    void findAll_returnsAllImages() throws Exception {
        mockMvc.perform(get("/api/images"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/images/count - returns count of images")
    void count_returnsCountOfImages() throws Exception {
        mockMvc.perform(get("/api/images/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("2"));
    }

    @Test
    @DisplayName("GET /api/images/{id} - returns image when exists")
    void findById_whenImageExists_returnsImage() throws Exception {
        mockMvc.perform(get("/api/images/{id}", savedImage.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/images/{id} - returns 404 when not found")
    void findById_whenImageNotFound_returns404() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(get("/api/images/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/images/{id} - deletes image")
    void delete_deletesImage() throws Exception {
        mockMvc.perform(delete("/api/images/{id}", savedImage.getId()))
                .andExpect(status().isNoContent());
        assertThat(imageRepository.findById(savedImage.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/images/{id} - returns 404 when not found")
    void delete_whenImageNotFound_returns404() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(delete("/api/images/{id}", randomId))
                .andExpect(status().isNotFound());
    }
}
