package com.trails_art.trails.services;

import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.mappers.ArtistMapper;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.models.Project;
import com.trails_art.trails.repositories.artist.JpaArtistRepository;
import com.trails_art.trails.services.artist.JpaArtistService;
import com.trails_art.trails.services.image.ImageService;
import com.trails_art.trails.services.location.LocationService;
import com.trails_art.trails.services.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static java.util.List.of;
import static java.util.UUID.randomUUID;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {
    @Mock private ProjectService projectService;
    @Mock private ImageService imageService;
    @Mock private LocationService locationService;
    @Mock private ArtistMapper artistMapper;
    @Mock private JpaArtistRepository jpaArtistRepository;
    @InjectMocks private JpaArtistService jpaArtistService;

    private UUID artistId;
    private UUID projectId;
    private UUID imageId;
    private UUID locationId;

    @BeforeEach
    void setUp() {
        artistId = randomUUID();
        projectId = randomUUID();
        imageId = randomUUID();
        locationId = randomUUID();
    }

    // Create methods for test data
    private Artist createArtist() {
        Artist artist = new Artist();
        artist.setId(artistId);
        return artist;
    }
    private Project createProject() {
        Project project = new Project();
        project.setId(projectId);
        return project;
    }
    private Image createImage() {
        Image image = new Image("image/png", Base64.getEncoder().encode("data".getBytes()));
        image.setId(imageId);
        return image;
    }
    private Location createLocation() {
        Location location = new Location("LocName", "LocAddr");
        location.setId(locationId);
        return location;
    }
    private com.trails_art.trails.dtos.ImageDto createImageDto() {
        return new com.trails_art.trails.dtos.ImageDto(imageId.toString(), "image/png", java.util.Base64.getEncoder().encodeToString("data".getBytes()));
    }
    private com.trails_art.trails.dtos.LocationDto createLocationDto() {
        return new com.trails_art.trails.dtos.LocationDto(null, "LocName", "LocAddr");
    }
    private com.trails_art.trails.dtos.ArtistImportDto.ProjectData createProjectData() {
        return new com.trails_art.trails.dtos.ArtistImportDto.ProjectData("ProjName", createLocationDto(), createImageDto(), "yt");
    }
    private com.trails_art.trails.dtos.ArtistImportDto createArtistImportDto(boolean isProjectExisting) {
        return new com.trails_art.trails.dtos.ArtistImportDto("ArtistName", createImageDto(), "desc", "insta", createProjectData(), isProjectExisting);
    }

    // CRUD Tests
    @Test
    @DisplayName("findById: returns artist when exists")
    void findById_whenArtistExists_returnsArtist() {
        Artist mockArtist = createArtist();
        
        when(jpaArtistRepository.findById(artistId)).thenReturn(Optional.of(mockArtist));
        Optional<Artist> result = jpaArtistService.findById(artistId);
        
        assertTrue(result.isPresent());
        assertEquals(artistId, result.get().getId());
        
        verify(jpaArtistRepository).findById(artistId);
        verifyNoMoreInteractions(jpaArtistRepository, projectService, imageService, locationService, artistMapper);
    }

    @Test
    @DisplayName("findById: throws when artist id not found")
    void findById_whenArtistIdNotFound_throwsException() {
        when(jpaArtistRepository.findById(artistId)).thenReturn(Optional.empty());

        InvalidArgumentIdException thrown = assertThrows(
                InvalidArgumentIdException.class,
                () -> jpaArtistService.findById(artistId)
        );
        assertEquals("Artist with ID " + artistId + " not found.", thrown.getMessage());

        verify(jpaArtistRepository).findById(artistId);
        verifyNoMoreInteractions(jpaArtistRepository, projectService, imageService, locationService, artistMapper);
    }

    @Test
    @DisplayName("update: throws when artist id not found")
    void update_whenArtistIdNotFound_throwsException() {
        Artist artist = createArtist();
        
        when(jpaArtistRepository.existsById(artistId)).thenReturn(false);
        
        InvalidArgumentIdException thrown = assertThrows(
                InvalidArgumentIdException.class,
                () -> jpaArtistService.update(artist, artistId)
        );
        assertEquals("Artist with ID " + artistId + " not found.", thrown.getMessage());
        
        verify(jpaArtistRepository).existsById(artistId);
        verify(jpaArtistRepository, never()).save(any());
        verifyNoMoreInteractions(jpaArtistRepository, projectService, imageService, locationService, artistMapper);
    }

    @Test
    @DisplayName("findAll: returns all artists")
    void findAll_returnsAllArtists() {
        when(jpaArtistRepository.findAll()).thenReturn(of(new Artist(), new Artist()));
        
        List<Artist> result = jpaArtistService.findAll();
        
        assertEquals(2, result.size());
        
        verify(jpaArtistRepository).findAll();
        verifyNoMoreInteractions(jpaArtistRepository, projectService, imageService, locationService, artistMapper);
    }

    @Test
    @DisplayName("create: saves artist")
    void create_savesArtist() {
        Artist artist = createArtist();
        
        jpaArtistService.create(artist);
        
        verify(jpaArtistRepository).save(artist);
        verifyNoMoreInteractions(jpaArtistRepository, projectService, imageService, locationService, artistMapper);
    }

    @Test
    @DisplayName("update: updates artist when exists")
    void update_whenArtistExists_updatesArtist() {
        Artist artist = new Artist();
        
        when(jpaArtistRepository.existsById(artistId)).thenReturn(true);
        
        jpaArtistService.update(artist, artistId);
        
        assertEquals(artistId, artist.getId());
        
        verify(jpaArtistRepository).save(artist);
        verify(jpaArtistRepository).existsById(artistId);
        verifyNoMoreInteractions(jpaArtistRepository, projectService, imageService, locationService, artistMapper);
    }

    @Test
    @DisplayName("delete: deletes artist and projects with no artists")
    void delete_deletesArtistAndDeletesProjectsWithNoArtists() {
        when(projectService.findAllWithEmptyArtists()).thenReturn(of());
        
        jpaArtistService.delete(artistId);
        
        verify(jpaArtistRepository).deleteById(artistId);
        verify(projectService).findAllWithEmptyArtists();
        verifyNoMoreInteractions(jpaArtistRepository, projectService, imageService, locationService, artistMapper);
    }

    // DTO Tests
    @Test
    @DisplayName("createFromDto: with new project creates artist and project")
    void createFromDto_withNewProject_createsArtistAndProject() {
        var dto = createArtistImportDto(false);
        var artist = createArtist();
        var project = createProject();
        var projectImage = createImage();
        var location = createLocation();
        project.setLocation(location);
        project.setImage(projectImage);

        when(artistMapper.mapToArtist(dto)).thenReturn(artist);
        when(artistMapper.mapToProject(dto.project())).thenReturn(project);
        
        jpaArtistService.createFromDto(dto);
        
        verify(jpaArtistRepository).save(artist);
        verify(imageService).create(project.getImage());
        verify(locationService).create(project.getLocation());
        verify(projectService).create(project);
        verify(jpaArtistRepository, atLeastOnce()).save(artist);
        verifyNoMoreInteractions(jpaArtistRepository, projectService, imageService, locationService, artistMapper);
    }

    @Test
    @DisplayName("createFromDto: with existing project adds artist to project")
    void createFromDto_withExistingProject_addsArtistToProject() {
        var dto = createArtistImportDto(true);
        var artist = createArtist();
        var project = createProject();
        var projectImage = createImage();
        var location = createLocation();
        project.setLocation(location);
        project.setImage(projectImage);
        var projects = of(project);
        
        when(artistMapper.mapToArtist(dto)).thenReturn(artist);
        when(projectService.findByName(dto.project().name())).thenReturn(projects);
        
        jpaArtistService.createFromDto(dto);
        
        verify(jpaArtistRepository).save(artist);
        verify(projectService).update(project, project.getId());
        verify(jpaArtistRepository, atLeastOnce()).save(artist);
        verifyNoMoreInteractions(jpaArtistRepository, projectService, imageService, locationService, artistMapper);
    }

    @Test
    @DisplayName("createFromDto: throws on invalid DTO")
    void createFromDto_invalidDto_throws() {
        var dto = mock(com.trails_art.trails.dtos.ArtistImportDto.class);
        
        when(artistMapper.mapToArtist(dto)).thenThrow(new RuntimeException("Invalid DTO"));
        
        assertThrows(RuntimeException.class, () -> jpaArtistService.createFromDto(dto));
        
        verify(artistMapper).mapToArtist(dto);
        verifyNoMoreInteractions(jpaArtistRepository, projectService, imageService, locationService, artistMapper);
    }

    @Test
    @DisplayName("updateFromDto: updates artist and image")
    void updateFromDto_updatesArtistAndImage() {
        var imageDto = createImageDto();
        var dto = new com.trails_art.trails.dtos.ArtistImportDto("ArtistName", imageDto, "desc", "insta", null, null);
        var artist = createArtist();
        var image = new com.trails_art.trails.models.Image();
        image.setId(imageId);
        artist.setImage(image);
        
        when(jpaArtistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(imageService.findById(imageId)).thenReturn(Optional.of(image));
        when(jpaArtistRepository.existsById(artistId)).thenReturn(true);
        
        jpaArtistService.updateFromDto(dto, artistId);
        
        assertEquals("ArtistName", artist.getName());
        assertEquals("desc", artist.getDescription());
        assertEquals("insta", artist.getInstagramUrl());
        assertEquals("image/png", image.getMimetype());
        
        verify(imageService).update(image, imageId);
        verify(jpaArtistRepository, atLeastOnce()).save(artist);
        verifyNoMoreInteractions(jpaArtistRepository, projectService, imageService, locationService, artistMapper);
    }

    @Test
    @DisplayName("updateFromDto: throws when artist id not found")
    void updateFromDto_whenArtistIdNotFound_throwsException() {
        var imageDto = createImageDto();
        var dto = new com.trails_art.trails.dtos.ArtistImportDto("ArtistName", imageDto, "desc", "insta", null, null);
        when(jpaArtistRepository.findById(artistId)).thenReturn(Optional.empty());

        InvalidArgumentIdException thrown = assertThrows(
                InvalidArgumentIdException.class,
                () -> jpaArtistService.updateFromDto(dto, artistId)
        );
        assertEquals("Artist with ID " + artistId + " not found.", thrown.getMessage());

        verify(jpaArtistRepository).findById(artistId);
        verifyNoMoreInteractions(jpaArtistRepository, projectService, imageService, locationService, artistMapper);
    }

    @Test
    @DisplayName("addProjects: adds projects to artist and updates")
    void addProjects_addsProjectsToArtistAndUpdates() {
        Artist artist = createArtist();
        Project project = createProject();
        List<UUID> projectIds = of(projectId);
        List<Project> projects = of(project);
        
        when(jpaArtistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(projectService.findAllByIdIn(projectIds)).thenReturn(projects);
        when(jpaArtistRepository.existsById(artistId)).thenReturn(true);
        
        jpaArtistService.addProjects(projectIds, artistId);
        
        assertTrue(artist.getProjects().contains(project));
        assertTrue(project.getArtists().contains(artist));
        
        verify(projectService).update(project, projectId);
        verify(jpaArtistRepository, atLeastOnce()).save(artist);
        verifyNoMoreInteractions(jpaArtistRepository, projectService, imageService, locationService, artistMapper);
    }

    @Test
    @DisplayName("addProjects: throws when artist id not found")
    void addProjects_whenArtistIdNotFound_throwsException() {
        List<UUID> projectIds = of(projectId);
        when(jpaArtistRepository.findById(artistId)).thenReturn(Optional.empty());

        InvalidArgumentIdException thrown = assertThrows(
                InvalidArgumentIdException.class,
                () -> jpaArtistService.addProjects(projectIds, artistId)
        );

        assertEquals("Artist with ID " + artistId + " not found.", thrown.getMessage());
        verify(jpaArtistRepository).findById(artistId);
        verifyNoMoreInteractions(jpaArtistRepository, projectService, imageService, locationService, artistMapper);
    }
}