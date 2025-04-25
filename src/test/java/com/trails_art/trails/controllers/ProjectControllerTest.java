package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.ProjectExportDto;
import com.trails_art.trails.dtos.ProjectImportDto;
import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.models.Project;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.services.artist.JpaArtistService;
import com.trails_art.trails.services.artist_project.JpaArtistProjectService;
import com.trails_art.trails.services.image.JpaImageService;
import com.trails_art.trails.services.location.JpaLocationService;
import com.trails_art.trails.services.project.JpaProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectControllerTest {

    private final JpaProjectService jpaProjectService = mock(JpaProjectService.class);
    private final JpaArtistService jpaArtistService = mock(JpaArtistService.class);
    private final JpaImageService jpaImageService = mock(JpaImageService.class);
    private final JpaLocationService jpaLocationService = mock(JpaLocationService.class);
    private final JpaArtistProjectService jpaArtistProjectService = mock(JpaArtistProjectService.class);

    private final ProjectController projectController = new ProjectController(
            jpaArtistService, jpaProjectService, jpaImageService, jpaLocationService, jpaArtistProjectService
    );

    @Test
    void testFindAll() {
        Project mockProject = new Project("Project Name", new Location("Location Name", "Map Address"),
                new Image("image/png", new byte[0]), "youtube_url");
        when(jpaProjectService.findAll()).thenReturn(List.of(mockProject));

        List<ProjectExportDto> result = projectController.findAll();
        assertEquals(1, result.size());
        assertEquals("Project Name", result.get(0).name());
        verify(jpaProjectService, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        UUID id = UUID.randomUUID();
        Project mockProject = new Project("Project Name", new Location("Location Name", "Map Address"),
                new Image("image/png", new byte[0]), "youtube_url");
        when(jpaProjectService.findById(id)).thenReturn(Optional.of(mockProject));

        ProjectExportDto result = projectController.findById(id);
        assertEquals("Project Name", result.name());
        verify(jpaProjectService, times(1)).findById(id);
    }

    @Test
    void testFindById_NotFound() {
        UUID id = UUID.randomUUID();
        when(jpaProjectService.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            projectController.findById(id);
        });
        assertEquals("Project not found.", exception.getReason());
        verify(jpaProjectService, times(1)).findById(id);
    }

    @Test
    void testCreate() {
        ProjectImportDto projectImportDto = new ProjectImportDto(
                "Project Name",
                new LocationDto("Location Name", "Map Address"),
                new ImageDto("image/png", Base64.getEncoder().encodeToString(new byte[0])),
                "youtube_url",
                Collections.emptyList(),
                Collections.emptyList()
        );

        projectController.create(projectImportDto);
        verify(jpaImageService, times(1)).create(any(Image.class));
        verify(jpaLocationService, times(1)).create(any(Location.class));
        verify(jpaProjectService, times(1)).create(any(Project.class));
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();

        projectController.delete(id.toString());
        verify(jpaProjectService, times(1)).delete(id);
    }

    @Test
    void testCount() {
        when(jpaProjectService.count()).thenReturn(5);

        int result = projectController.count();
        assertEquals(5, result);
        verify(jpaProjectService, times(1)).count();
    }

    @Test
    void testFindByName() {
        String name = "Project Name";
        Project mockProject = new Project(name, new Location("Location Name", "Map Address"),
                new Image("image/png", new byte[0]), "youtube_url");
        when(jpaProjectService.findByName(name)).thenReturn(List.of(mockProject));

        List<ProjectExportDto> result = projectController.findByName(name);
        assertEquals(1, result.size());
        assertEquals(name, result.get(0).name());
        verify(jpaProjectService, times(1)).findByName(name);
    }
}
