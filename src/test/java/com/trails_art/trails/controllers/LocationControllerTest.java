package com.trails_art.trails.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trails_art.trails.models.Location;
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

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class LocationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaLocationRepository locationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Location savedLocation;
    private Location savedLocation2;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
        Location loc1 = locationRepository.save(new Location("Loc1", "Addr1"));
        Location loc2 = locationRepository.save(new Location("Loc2", "Addr2"));
        List<Location> all = locationRepository.findAll();
        savedLocation = all.get(0);
        savedLocation2 = all.get(1);
    }

    @Test
    @DisplayName("GET /api/locations - returns all locations")
    void findAll_returnsAllLocations() throws Exception {
        mockMvc.perform(get("/api/locations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/locations/count - returns count of locations")
    void count_returnsCountOfLocations() throws Exception {
        mockMvc.perform(get("/api/locations/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("2"));
    }

    @Test
    @DisplayName("GET /api/locations/{id} - returns location when exists")
    void findById_whenLocationExists_returnsLocation() throws Exception {
        mockMvc.perform(get("/api/locations/{id}", savedLocation.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/locations/{id} - returns 404 when not found")
    void findById_whenLocationNotFound_returns404() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(get("/api/locations/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/locations/{id} - deletes location")
    void delete_deletesLocation() throws Exception {
        mockMvc.perform(delete("/api/locations/{id}", savedLocation.getId()))
                .andExpect(status().isNoContent());
        assertThat(locationRepository.findById(savedLocation.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/locations/{id} - returns 404 when not found")
    void delete_whenLocationNotFound_returns404() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(delete("/api/locations/{id}", randomId))
                .andExpect(status().isNotFound());
    }
}
