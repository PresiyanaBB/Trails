package com.trails_art.trails.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trails_art.trails.dtos.ArtistImportDto;
import com.trails_art.trails.dtos.export.ExportDtoMethods;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.models.Project;
import com.trails_art.trails.repositories.artist.JpaArtistRepository;
import com.trails_art.trails.repositories.JpaImageRepository;
import com.trails_art.trails.repositories.project.JpaProjectRepository;
import com.trails_art.trails.repositories.JpaLocationRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class ArtistControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaArtistRepository artistRepository;

    @Autowired
    private JpaImageRepository imageRepository;

    @Autowired
    private JpaProjectRepository projectRepository;

    @Autowired
    private JpaLocationRepository locationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Artist savedArtist;
    private Artist savedArtist2;

    private Project savedProject;
    private Project savedProject2;

    private String artistJson;

    @BeforeEach
    void setUp() {
        artistRepository.deleteAll();
        projectRepository.deleteAll();
        imageRepository.deleteAll();
        locationRepository.deleteAll();

        Image savedImage = imageRepository.save(new Image("image/png", "test-image".getBytes(StandardCharsets.UTF_8)));
        Artist artist = new Artist("Test Artist", savedImage, "desc", "insta");

        Image savedArtist2Image = new Image("image/png", "test-image2".getBytes(StandardCharsets.UTF_8));
        Artist artist2 = new Artist("Test Artist2", savedArtist2Image, "desc", "insta");

        Location location = locationRepository.save(new Location("name","addr"));
        Image savedProjectImage = imageRepository.save(new Image("image/png", "test-image".getBytes(StandardCharsets.UTF_8)));
        Project project = new Project("project",location,savedProjectImage,"https://youtube.com/project");

        Image savedProject2Image = imageRepository.save(new Image("image/png", "test-image".getBytes(StandardCharsets.UTF_8)));
        Location location2 = locationRepository.save(new Location("name2","addr2"));
        Project project2 = new Project("project2",location2,savedProject2Image,"https://youtube.com/project2");


        artist.setProjects(List.of(project,project2));
        project.setArtists(List.of(artist));
        artist2.setProjects(List.of(project2));
        project2.setArtists(List.of(artist,artist2));

        artistRepository.save(artist);
        artistRepository.save(artist2);
        projectRepository.save(project);
        projectRepository.save(project2);

        savedArtist = artistRepository.findAll().get(0);
        savedArtist2 = artistRepository.findAll().get(1);
        savedProject = projectRepository.findAll().get(0);
        savedProject2 = projectRepository.findAll().get(1);

        artistJson = "{\n" +
                "  \"name\": \"Gosho Pochivka\",\n" +
                "  \"image\": {\n" +
                "    \"mimetype\": \"image/jpeg\",\n" +
                "    \"data\": \"/1j/4AAQSkZJRgABAQEAAAAAAAD/2wBDAAoHBwkJCB0LCwsOCw8PEh4SEh8h\"\n" +
                "  },\n" +
                "  \"description\": \"A passionate street artist specializing in graffiti murals.\",\n" +
                "  \"instagram_url\": \"https://instagram.com/artist_one\",\n" +
                "  \"projects\": [\n" +
                "    {\n" +
                "      \"name\": \"Graffiti Revival\",\n" +
                "      \"location\": {\n" +
                "        \"name\": \"Ulica nz, Sofia\",\n" +
                "        \"map_address\": \"45'65'89\"\n" +
                "      },\n" +
                "      \"image\": {\n" +
                "        \"mimetype\": \"image/jpg\",\n" +
                "        \"data\": \"/2j/4AAQSkZJRgABAQEAAAAAAAD/2wBDAAoHBwkJCB0LCwsOCw8PEh4SEh8h\"\n" +
                "      },\n" +
                "      \"youtube_url\": \"https://youtube.com/watch?v=sample123\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"is_project_existing\" : [\n" +
                "    false\n" +
                "  ]\n" +
                "}";
    }

    @Test
    @DisplayName("GET /api/artists - returns all artists")
    void findAll_returnsAllArtists() throws Exception {
        mockMvc.perform(get("/api/artists"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/artists/{id} - returns artist when exists")
    void findById_whenArtistExists_returnsArtist() throws Exception {
        mockMvc.perform(get("/api/artists/{id}", savedArtist.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/artists/{id} - returns 404 when not found")
    void findById_whenArtistNotFound_returns404() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(get("/api/artists/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/artists/count - returns artist count")
    void count_returnsArtistCount() throws Exception {
        mockMvc.perform(get("/api/artists/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    @DisplayName("GET /api/artists/name/{name} - returns artists by name")
    void findByName_returnsArtistsByName() throws Exception {
        mockMvc.perform(get("/api/artists/name/{name}", "Test Artist"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("DELETE /api/artists/{id} - deletes artist")
    void delete_deletesArtist() throws Exception {
        mockMvc.perform(delete("/api/artists/{id}", savedArtist.getId()))
                .andExpect(status().isNoContent());
        assertThat(artistRepository.findById(savedArtist.getId())).isEmpty();
        assertThat(projectRepository.findById(savedProject.getId())).isEmpty();
        assertThat(projectRepository.findById(savedProject2.getId())).isPresent();
    }

    @Test
    @DisplayName("DELETE /api/artists/{id} - returns 404 when not found")
    void delete_whenArtistNotFound_returns404() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(delete("/api/artists/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/artists/{id} - deletes project if artist is the only one in project")
    void delete_deletesProjectIfOnlyArtist() throws Exception {
        assertThat(artistRepository.findById(savedArtist.getId())).isPresent();
        assertThat(projectRepository.findById(savedProject.getId())).isPresent();

        mockMvc.perform(delete("/api/artists/{id}", savedArtist.getId()))
                .andExpect(status().isNoContent());

        assertThat(artistRepository.findById(savedArtist.getId())).isEmpty();
        assertThat(projectRepository.findById(savedProject.getId())).isEmpty();
        assertEquals(1, artistRepository.findById(savedArtist2.getId()).get().getProjects().size());
        assertEquals(1, projectRepository.findById(savedProject2.getId()).get().getArtists().size());
    }

    @Test
    @DisplayName("POST /api/artists - returns 400 for invalid payload")
    void create_withInvalidPayload_returns400() throws Exception {
        String artistJson = "{\"name\": \"\", \"image\": null, \"description\": \"\", \"instagram_url\": \"\", \"project\": null, \"is_project_existing\": false}";
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(artistJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("PUT /api/artists/{id} - returns 404 for non-existent artist")
    void update_withNonExistentArtist_returns404() throws Exception {
        UUID randomId = UUID.randomUUID();
        String imageBase64 = java.util.Base64.getEncoder().encodeToString("img".getBytes(StandardCharsets.UTF_8));
        String artistJson = objectMapper.writeValueAsString(new com.trails_art.trails.dtos.ArtistImportDto(
                "Name",
                new com.trails_art.trails.dtos.ImageDto(null, "image/png", imageBase64),
                "desc",
                "insta",
                null,
                true
        ));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/artists/{id}", randomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(artistJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/artists/add-projects/{id} - returns 404 for non-existent artist")
    void addProjects_withNonExistentArtist_returns404() throws Exception {
        UUID randomId = UUID.randomUUID();
        java.util.List<String> projectIds = java.util.List.of(savedProject2.getId().toString());
        String json = objectMapper.writeValueAsString(projectIds);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/artists/add-projects/{id}", randomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }
}
