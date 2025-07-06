package com.trails_art.trails.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trails_art.trails.models.Event;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.repositories.JpaEventRepository;
import com.trails_art.trails.repositories.JpaImageRepository;
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
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaEventRepository eventRepository;

    @Autowired
    private JpaImageRepository imageRepository;

    @Autowired
    private JpaLocationRepository locationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Event savedEvent;
    private Event savedEvent2;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        imageRepository.deleteAll();
        locationRepository.deleteAll();

        Image image1 = imageRepository.save(new Image("image/png", "test-image1".getBytes(StandardCharsets.UTF_8)));
        Location location1 = locationRepository.save(new Location("Location1", "Address1"));
        Event event1 = new Event("Event1", "Description1", image1, LocalDateTime.now(), LocalDateTime.now().plusHours(2), location1);

        Image image2 = imageRepository.save(new Image("image/png", "test-image2".getBytes(StandardCharsets.UTF_8)));
        Location location2 = locationRepository.save(new Location("Location2", "Address2"));
        Event event2 = new Event("Event2", "Description2", image2, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), location2);

        eventRepository.save(event1);
        eventRepository.save(event2);

        savedEvent = eventRepository.findAll().get(0);
        savedEvent2 = eventRepository.findAll().get(1);
    }

    @Test
    @DisplayName("GET /api/events - returns all events")
    void findAll_returnsAllEvents() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    @DisplayName("GET /api/events/{id} - returns event when exists")
    void findById_whenEventExists_returnsEvent() throws Exception {
        mockMvc.perform(get("/api/events/{id}", savedEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/events/{id} - returns 404 when not found")
    void findById_whenEventNotFound_returns404() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(get("/api/events/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/events/{id} - deletes event")
    void delete_deletesEvent() throws Exception {
        mockMvc.perform(delete("/api/events/{id}", savedEvent.getId()))
                .andExpect(status().isNoContent());
        assertThat(eventRepository.findById(savedEvent.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/events/{id} - returns 404 when not found")
    void delete_whenEventNotFound_returns404() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(delete("/api/events/{id}", randomId))
                .andExpect(status().isNotFound());
    }
}
