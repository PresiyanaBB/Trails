package com.trails_art.trails.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.models.Project;
import com.trails_art.trails.repositories.JpaImageRepository;
import com.trails_art.trails.repositories.JpaLocationRepository;
import com.trails_art.trails.repositories.project.JpaProjectRepository;
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
import com.trails_art.trails.models.Artist;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaProjectRepository projectRepository;

    @Autowired
    private JpaImageRepository imageRepository;

    @Autowired
    private JpaLocationRepository locationRepository;

    @Autowired
    private com.trails_art.trails.repositories.artist.JpaArtistRepository artistRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Artist savedArtist;
    private Artist savedArtist2;

    private Project savedProject;
    private Project savedProject2;

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
    }

    @Test
    @DisplayName("GET /api/projects - returns all projects")
    void findAll_returnsAllProjects() throws Exception {
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/projects/{id} - returns project when exists")
    void findById_whenProjectExists_returnsProject() throws Exception {
        mockMvc.perform(get("/api/projects/{id}", savedProject.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/projects/{id} - returns 404 when not found")
    void findById_whenProjectNotFound_returns404() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(get("/api/projects/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} - deletes project")
    void delete_deletesProject() throws Exception {
        mockMvc.perform(delete("/api/projects/{id}", savedProject.getId()))
                .andExpect(status().isNoContent());
        Optional<Project> deleted = projectRepository.findById(savedProject.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} - returns 404 when not found")
    void delete_whenProjectNotFound_returns404() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(delete("/api/projects/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} - removes project from artists and deletes artists with no projects")
    void delete_removesProjectFromArtistsAndDeletesOrphanArtists() throws Exception {
        assertThat(artistRepository.findById(savedArtist.getId())).isPresent();
        assertThat(artistRepository.findById(savedArtist2.getId())).isPresent();
        assertThat(projectRepository.findById(savedProject.getId())).isPresent();
        assertThat(projectRepository.findById(savedProject2.getId())).isPresent();

        mockMvc.perform(delete("/api/projects/{id}", savedProject2.getId()))
                .andExpect(status().isNoContent());

        assertThat(projectRepository.findById(savedProject2.getId())).isEmpty();
        assertThat(artistRepository.findById(savedArtist2.getId())).isEmpty();
        assertThat(artistRepository.findById(savedArtist.getId())).isPresent();
        assertThat(projectRepository.findById(savedProject.getId())).isPresent();
        assertEquals(1,projectRepository.findById(savedProject.getId()).get().getArtists().size());
        assertEquals(1,artistRepository.findById(savedArtist.getId()).get().getProjects().size());
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} - deletes artist if project is the only one for artist")
    void delete_deletesArtistIfOnlyProject() throws Exception {
        assertThat(artistRepository.findById(savedArtist.getId())).isPresent();
        assertThat(projectRepository.findById(savedProject.getId())).isPresent();
        assertThat(artistRepository.findById(savedArtist2.getId())).isPresent();
        assertThat(projectRepository.findById(savedProject2.getId())).isPresent();

        mockMvc.perform(delete("/api/projects/{id}", savedProject2.getId()))
                .andExpect(status().isNoContent());

        assertThat(projectRepository.findById(savedProject2.getId())).isEmpty();
        assertThat(artistRepository.findById(savedArtist2.getId())).isEmpty();
        assertEquals(1,projectRepository.findById(savedProject.getId()).get().getArtists().size());
        assertEquals(1,artistRepository.findById(savedArtist.getId()).get().getProjects().size());
    }
}
