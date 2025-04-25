package com.trails_art.trails.controllers;

import com.trails_art.trails.models.Image;
import com.trails_art.trails.services.image.JpaImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ImageControllerTest {

    @Mock
    private JpaImageService jpaImageService;

    @InjectMocks
    private ImageController imageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();
    }

    @Test
    void testFindAll() throws Exception {
        List<Image> images = List.of(new Image("image/jpeg", new byte[]{}));
        when(jpaImageService.findAll()).thenReturn(images);

        mockMvc.perform(get("/api/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mimetype").value("image/jpeg"));
    }

    @Test
    void testFindById() throws Exception {
        UUID id = UUID.randomUUID();
        Image image = new Image("image/jpeg", new byte[]{});
        when(jpaImageService.findById(id)).thenReturn(Optional.of(image));

        mockMvc.perform(get("/api/images/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mimetype").value("image/jpeg"));
    }

    @Test
    void testFindById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(jpaImageService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/images/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {
        mockMvc.perform(post("/api/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mimetype\":\"image/jpeg\",\"data\":\"\"}"))
                .andExpect(status().isCreated());

        verify(jpaImageService, times(1)).create(any());
    }

    @Test
    void testUpdate() throws Exception {
        UUID id = UUID.randomUUID();
        Image existingImage = new Image("image/jpeg", new byte[]{});
        when(jpaImageService.findById(id)).thenReturn(Optional.of(existingImage));

        mockMvc.perform(put("/api/images/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mimetype\":\"image/png\",\"data\":\"\"}"))
                .andExpect(status().isNoContent());

        verify(jpaImageService, times(1)).update(any(), eq(id));
    }

    @Test
    void testDelete() throws Exception {
        String id = UUID.randomUUID().toString();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/images/" + id))
                .andExpect(status().isNoContent());

        verify(jpaImageService, times(1)).delete(UUID.fromString(id));
    }

    @Test
    void testCount() throws Exception {
        when(jpaImageService.count()).thenReturn(5);

        mockMvc.perform(get("/api/images/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
}
