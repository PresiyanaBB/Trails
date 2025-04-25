package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.ArtistExportDto;
import com.trails_art.trails.dtos.ArtistImportDto;
import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.models.Project;
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

class ArtistControllerTest {

    private final JpaArtistService jpaArtistService = mock(JpaArtistService.class);
    private final JpaProjectService jpaProjectService = mock(JpaProjectService.class);
    private final JpaImageService jpaImageService = mock(JpaImageService.class);
    private final JpaLocationService jpaLocationService = mock(JpaLocationService.class);
    private final JpaArtistProjectService jpaArtistProjectService = mock(JpaArtistProjectService.class);

    private final ArtistController artistController = new ArtistController(
            jpaArtistService, jpaProjectService, jpaImageService, jpaLocationService, jpaArtistProjectService
    );

    @Test
    void testFindAll() {
        Artist mockArtist = new Artist("Artist Name", new Image("image/png", new byte[0]), "Description", "Instagram");
        when(jpaArtistService.findAll()).thenReturn(List.of(mockArtist));

        List<ArtistExportDto> result = artistController.findAll();
        assertEquals(1, result.size());
        assertEquals("Artist Name", result.get(0).name());
        verify(jpaArtistService, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        UUID id = UUID.randomUUID();
        Artist mockArtist = new Artist("Artist Name", new Image("image/png", new byte[0]), "Description", "Instagram");
        when(jpaArtistService.findById(id)).thenReturn(Optional.of(mockArtist));

        ArtistExportDto result = artistController.findById(id);
        assertEquals("Artist Name", result.name());
        verify(jpaArtistService, times(1)).findById(id);
    }

    @Test
    void testFindById_NotFound() {
        UUID id = UUID.randomUUID();
        when(jpaArtistService.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            artistController.findById(id);
        });
        assertEquals("Artist not found.", exception.getReason());
        verify(jpaArtistService, times(1)).findById(id);
    }

    @Test
    void testCreate() {
        ArtistImportDto.ProjectData projectData = new ArtistImportDto.ProjectData(
                "Project Name",
                new LocationDto("Location Name", "Map Address"),
                new ImageDto("image/png", Base64.getEncoder().encodeToString(new byte[0])),
                "youtube_url"
        );
        ArtistImportDto artistImportDto = new ArtistImportDto(
                "Artist Name",
                new ImageDto("image/png", Base64.getEncoder().encodeToString(new byte[0])),
                "Description",
                "Instagram",
                List.of(projectData),
                List.of(false)
        );

        artistController.create(artistImportDto);
        verify(jpaArtistService, times(1)).create(any(Artist.class));
        verify(jpaImageService, times(2)).create(any(Image.class)); // One for artist, one for project
        verify(jpaLocationService, times(1)).create(any(Location.class));
        verify(jpaProjectService, times(1)).create(any(Project.class));
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();

        artistController.delete(id.toString());
        verify(jpaArtistService, times(1)).delete(id);
    }

    @Test
    void testCount() {
        when(jpaArtistService.count()).thenReturn(10);

        int result = artistController.count();
        assertEquals(10, result);
        verify(jpaArtistService, times(1)).count();
    }

    @Test
    void testFindByName() {
        String name = "Artist Name";
        Artist mockArtist = new Artist(name, new Image("image/png", new byte[0]), "Description", "Instagram");
        when(jpaArtistService.findByName(name)).thenReturn(List.of(mockArtist));

        List<ArtistExportDto> result = artistController.findByName(name);
        assertEquals(1, result.size());
        assertEquals(name, result.get(0).name());
        verify(jpaArtistService, times(1)).findByName(name);
    }
}
