package com.trails_art.trails.services;

import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.mappers.ArtistMapper;
import com.trails_art.trails.mappers.ProjectMapper;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.models.Project;
import com.trails_art.trails.repositories.project.JpaProjectRepository;
import com.trails_art.trails.services.artist.ArtistService;
import com.trails_art.trails.services.image.ImageService;
import com.trails_art.trails.services.location.LocationService;
import com.trails_art.trails.services.project.JpaProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {
    @Mock
    private JpaProjectRepository jpaProjectRepository;
    @Mock
    private ArtistService artistService;
    @Mock
    private ImageService imageService;
    @Mock
    private LocationService locationService;

    @InjectMocks
    private JpaProjectService jpaProjectService;

    // Common test data
    private com.trails_art.trails.dtos.ImageDto imageDto;
    private com.trails_art.trails.dtos.LocationDto locationDto;
    private com.trails_art.trails.dtos.ArtistDataDto artistData;
    private com.trails_art.trails.dtos.ProjectImportDto projectImportDtoNewArtist;
    private com.trails_art.trails.dtos.ProjectImportDto projectImportDtoExistingArtist;
    private Project project;
    private Artist artist;
    private Image image;
    private Location location;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        imageDto = createImageDto();
        locationDto = createLocationDto();
        artistData = createArtistData();
        projectImportDtoNewArtist = createProjectImportDto(false);
        projectImportDtoExistingArtist = createProjectImportDto(true);
        project = createProject();
        artist = createArtist();
        image = createImage();
        location = createLocation();
    }

    // Helper methods
    private com.trails_art.trails.dtos.ImageDto createImageDto() {
        return new com.trails_art.trails.dtos.ImageDto(randomUUID().toString(), "image/png", "aGVsbG8=");
    }

    private com.trails_art.trails.dtos.LocationDto createLocationDto() {
        return new com.trails_art.trails.dtos.LocationDto(randomUUID().toString(), "LocName", "Address");
    }

    private com.trails_art.trails.dtos.ArtistDataDto createArtistData() {
        return new com.trails_art.trails.dtos.ArtistDataDto("ArtistName", imageDto, "desc", "insta");
    }

    private com.trails_art.trails.dtos.ProjectImportDto createProjectImportDto(boolean isArtistExisting) {
        return new com.trails_art.trails.dtos.ProjectImportDto("ProjectName", locationDto, imageDto, "yt", artistData, isArtistExisting);
    }

    private Project createProject() {
        Project p = new Project();
        p.setId(randomUUID());
        p.setName("ProjectName");
        p.setImage(image);
        p.setLocation(location);
        p.setYoutubeUrl("https://youtube.com/some-project");
        return p;
    }

    private Artist createArtist() {
        Artist a = new Artist();
        a.setId(randomUUID());
        a.setName("ArtistName");
        a.setInstagramUrl("https://instagram.com/some-artist");
        a.setDescription("desc");
        a.setImage(image);
        return a;
    }

    private Image createImage() {
        Image img = new Image();
        img.setId(randomUUID());
        img.setMimetype("image/png");
        img.setData("hello".getBytes());
        return img;
    }

    private Location createLocation() {
        Location loc = new Location();
        loc.setId(randomUUID());
        loc.setName("LocName");
        loc.setMapAddress("Address");
        return loc;
    }

    @Test
    @DisplayName("create: saves project")
    void create_WithValidProject_SavesProject() {
        jpaProjectService.create(project);
        verify(jpaProjectRepository, times(1)).save(project);
    }

    @Test
    @DisplayName("findAll: returns all projects from repository")
    void findAll_WhenProjectsExist_ReturnsAllProjects() {
        List<Project> projects = Arrays.asList(new Project(), new Project());

        when(jpaProjectRepository.findAll()).thenReturn(projects);
        List<Project> result = jpaProjectService.findAll();

        assertEquals(2, result.size());
        verify(jpaProjectRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById: finds a project by its ID")
    void findById_WithValidId_ReturnsProject() {
        UUID id = randomUUID();
        when(jpaProjectRepository.findById(id)).thenReturn(Optional.of(project));

        Optional<Project> result = jpaProjectService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(project, result.get());
        verify(jpaProjectRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("update: updates a project when it exists")
    void update_WhenProjectExists_UpdatesProject() {
        UUID id = randomUUID();
        when(jpaProjectRepository.existsById(id)).thenReturn(true);
        jpaProjectService.update(project, id);

        verify(jpaProjectRepository, times(1)).save(project);
        assertEquals(id, project.getId());
    }

    @Test
    @DisplayName("createFromDto: with new artist creates project and artist")
    void createFromDto_WithNewArtist_CreatesProjectAndArtist() {
        try (MockedStatic<ProjectMapper> projectMapperMock = mockStatic(ProjectMapper.class);
             MockedStatic<ArtistMapper> artistMapperMock = mockStatic(ArtistMapper.class)) {
            
            projectMapperMock.when(() -> ProjectMapper.mapToProject(projectImportDtoNewArtist)).thenReturn(project);
            artistMapperMock.when(() -> ArtistMapper.mapToArtist(artistData)).thenReturn(artist);
            
            doNothing().when(artistService).create(artist);
            doNothing().when(artistService).update(any(Artist.class), any());
            when(jpaProjectRepository.save(any(Project.class))).thenReturn(project);
            when(jpaProjectRepository.existsById(any())).thenReturn(true);
            
            jpaProjectService.createFromDto(projectImportDtoNewArtist);

            projectMapperMock.verify(() -> ProjectMapper.mapToProject(projectImportDtoNewArtist));
            artistMapperMock.verify(() -> ArtistMapper.mapToArtist(artistData));
            verify(artistService).create(artist);
            verify(jpaProjectRepository, atLeastOnce()).save(any(Project.class));
        }
    }

    @Test
    @DisplayName("createFromDto: with existing artist adds artist to project")
    void createFromDto_WithExistingArtist_AddsArtistToProject() {
        try (MockedStatic<ProjectMapper> projectMapperMock = mockStatic(ProjectMapper.class)) {
            projectMapperMock.when(() -> ProjectMapper.mapToProject(projectImportDtoExistingArtist)).thenReturn(project);
            
            when(artistService.findByName(artistData.name())).thenReturn(List.of(artist));
            doNothing().when(artistService).update(any(Artist.class), any());
            when(jpaProjectRepository.save(any(Project.class))).thenReturn(project);
            when(jpaProjectRepository.existsById(any())).thenReturn(true);
            
            jpaProjectService.createFromDto(projectImportDtoExistingArtist);

            projectMapperMock.verify(() -> ProjectMapper.mapToProject(projectImportDtoExistingArtist));
            verify(artistService).findByName(artistData.name());
            verify(jpaProjectRepository, atLeastOnce()).save(any(Project.class));
        }
    }

    @Test
    @DisplayName("updateFromDto: updates project and related image and location")
    void updateFromDto_WithValidDto_UpdatesProjectImageAndLocation() {
        UUID id = randomUUID();
        project.setImage(image);
        project.setLocation(location);

        when(jpaProjectRepository.findById(id)).thenReturn(Optional.of(project));
        when(imageService.findById(image.getId())).thenReturn(Optional.of(image));
        when(locationService.findById(location.getId())).thenReturn(Optional.of(location));
        when(jpaProjectRepository.existsById(id)).thenReturn(true);
        doNothing().when(imageService).update(any(Image.class), any());
        doNothing().when(locationService).update(any(Location.class), any());
        when(jpaProjectRepository.save(any(Project.class))).thenReturn(project);
        jpaProjectService.updateFromDto(projectImportDtoExistingArtist, id);

        verify(imageService).findById(image.getId());
        verify(locationService).findById(location.getId());
        verify(imageService).update(any(Image.class), any());
        verify(locationService).update(any(Location.class), any());
        verify(jpaProjectRepository, atLeastOnce()).save(any(Project.class));
    }

    @Test
    @DisplayName("findAllByIdIn: finds all projects by a list of IDs")
    void findAllByIdIn_WithValidIds_ReturnsProjects() {
        List<UUID> ids = List.of(randomUUID(), randomUUID());
        List<Project> projects = Arrays.asList(new Project(), new Project());

        when(jpaProjectRepository.findAllById(ids)).thenReturn(projects);
        List<Project> result = jpaProjectService.findAllByIdIn(ids);

        assertEquals(2, result.size());
        verify(jpaProjectRepository).findAllById(ids);
    }

    @Test
    @DisplayName("findAllWithEmptyArtists: finds all projects with empty artists")
    void findAllWithEmptyArtists_WhenCalled_ReturnsProjectsWithNoArtists() {
        List<Project> projects = Arrays.asList(new Project(), new Project());
        when(jpaProjectRepository.findAllWithEmptyArtists()).thenReturn(projects);
        List<Project> result = jpaProjectService.findAllWithEmptyArtists();

        assertEquals(2, result.size());
        verify(jpaProjectRepository).findAllWithEmptyArtists();
    }

    @Test
    @DisplayName("count: returns the count of projects")
    void count_WhenCalled_ReturnsProjectCount() {
        when(jpaProjectRepository.count()).thenReturn(5L);
        int count = jpaProjectService.count();

        assertEquals(5, count);
        verify(jpaProjectRepository).count();
    }

    @Test
    @DisplayName("saveAll: saves all projects in a batch")
    void saveAll_WithProjects_SavesAllProjects() {
        List<Project> projects = Arrays.asList(new Project(), new Project());
        jpaProjectService.saveAll(projects);
        verify(jpaProjectRepository).saveAll(projects);
    }

    @Test
    @DisplayName("findByName: finds projects by name (case-insensitive contains)")
    void findByName_WithName_ReturnsMatchingProjects() {
        String name = "test";
        List<Project> projects = Arrays.asList(new Project(), new Project());

        when(jpaProjectRepository.findByNameContainingIgnoreCase(name)).thenReturn(projects);
        List<Project> result = jpaProjectService.findByName(name);

        assertEquals(2, result.size());
        verify(jpaProjectRepository).findByNameContainingIgnoreCase(name);
    }

    @Test
    @DisplayName("addNonExistingArtist: adds a non-existing artist to a project")
    void addNonExistingArtist_WithValidArtist_AddsArtistToProject() {
        UUID projectId = randomUUID();
        Project project = new Project();
        project.setId(projectId);
        Artist artist = new Artist();

        when(jpaProjectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(jpaProjectRepository.existsById(projectId)).thenReturn(true);
        doNothing().when(artistService).update(artist, artist.getId());
        when(jpaProjectRepository.save(any(Project.class))).thenReturn(project);
        jpaProjectService.addNonExistingArtist(projectId, artist);

        assertTrue(project.getArtists().contains(artist));
        assertTrue(artist.getProjects().contains(project));
        verify(artistService).update(artist, artist.getId());
        verify(jpaProjectRepository, atLeastOnce()).save(any(Project.class));
    }

    @Test
    @DisplayName("addExistingArtist: adds an existing artist to a project")
    void addExistingArtist_WithValidArtistId_AddsArtistToProject() {
        UUID projectId = randomUUID();
        UUID artistId = randomUUID();
        Project project = new Project();
        project.setId(projectId);
        Artist artist = new Artist();
        artist.setId(artistId);

        when(jpaProjectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(artistService.findById(artistId)).thenReturn(Optional.of(artist));
        when(jpaProjectRepository.existsById(projectId)).thenReturn(true);
        doNothing().when(artistService).create(artist);
        when(jpaProjectRepository.save(any(Project.class))).thenReturn(project);
        jpaProjectService.addExistingArtist(projectId, artistId);

        assertTrue(project.getArtists().contains(artist));
        assertTrue(artist.getProjects().contains(project));
        verify(artistService).create(artist);
        verify(jpaProjectRepository, atLeastOnce()).save(any(Project.class));
    }

    @Test
    @DisplayName("deleteArtist: deletes an artist from a project")
    void deleteArtist_WithValidIds_RemovesArtistFromProject() {
        UUID projectId = randomUUID();
        UUID artistId = randomUUID();
        Project project = new Project();
        project.setId(projectId);
        Artist artist = new Artist();
        artist.setId(artistId);
        project.getArtists().add(artist);
        artist.getProjects().add(project);

        when(jpaProjectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(artistService.findById(artistId)).thenReturn(Optional.of(artist));
        when(jpaProjectRepository.existsById(projectId)).thenReturn(true);
        doNothing().when(artistService).delete(artistId);
        when(jpaProjectRepository.save(any(Project.class))).thenReturn(project);
        jpaProjectService.deleteArtist(projectId, artistId);

        assertFalse(project.getArtists().contains(artist));
        assertFalse(artist.getProjects().contains(project));
        verify(artistService).delete(artistId);
        verify(jpaProjectRepository, atLeastOnce()).save(any(Project.class));
    }

    @Test
    @DisplayName("update: throws InvalidArgumentIdException when project does not exist")
    void update_WhenProjectDoesNotExist_ThrowsException() {
        UUID invalidId = randomUUID();
        when(jpaProjectRepository.existsById(invalidId)).thenReturn(false);
        InvalidArgumentIdException thrown = assertThrows(
            InvalidArgumentIdException.class,
            () -> jpaProjectService.update(project, invalidId)
        );
        assertEquals("Project with ID " + invalidId + " not found.", thrown.getMessage());
    }

    @Test
    @DisplayName("updateFromDto: throws InvalidArgumentIdException when project does not exist")
    void updateFromDto_WhenProjectDoesNotExist_ThrowsException() {
        UUID invalidId = randomUUID();
        when(jpaProjectRepository.findById(invalidId)).thenReturn(Optional.empty());
        InvalidArgumentIdException thrown = assertThrows(
            InvalidArgumentIdException.class,
            () -> jpaProjectService.updateFromDto(projectImportDtoExistingArtist, invalidId)
        );
        assertEquals("Project with ID " + invalidId + " not found.", thrown.getMessage());
    }

    @Test
    @DisplayName("addNonExistingArtist: throws InvalidArgumentIdException when project does not exist")
    void addNonExistingArtist_WhenProjectDoesNotExist_ThrowsException() {
        UUID invalidId = randomUUID();
        when(jpaProjectRepository.findById(invalidId)).thenReturn(Optional.empty());
        InvalidArgumentIdException thrown = assertThrows(
            InvalidArgumentIdException.class,
            () -> jpaProjectService.addNonExistingArtist(invalidId, artist)
        );
        assertEquals("Project with ID " + invalidId + " not found.", thrown.getMessage());
    }

    @Test
    @DisplayName("addExistingArtist: throws InvalidArgumentIdException when project or artist does not exist")
    void addExistingArtist_WhenProjectOrArtistDoesNotExist_ThrowsException() {
        UUID invalidProjectId = randomUUID();
        UUID invalidArtistId = randomUUID();
        // Project not found
        when(jpaProjectRepository.findById(invalidProjectId)).thenReturn(Optional.empty());
        InvalidArgumentIdException thrown1 = assertThrows(
            InvalidArgumentIdException.class,
            () -> jpaProjectService.addExistingArtist(invalidProjectId, invalidArtistId)
        );
        assertEquals("Project with ID " + invalidProjectId + " not found.", thrown1.getMessage());
        // Project found, artist not found
        when(jpaProjectRepository.findById(invalidProjectId)).thenReturn(Optional.of(project));
        when(artistService.findById(invalidArtistId)).thenReturn(Optional.empty());
        InvalidArgumentIdException thrown2 = assertThrows(
            InvalidArgumentIdException.class,
            () -> jpaProjectService.addExistingArtist(invalidProjectId, invalidArtistId)
        );
        assertEquals("Artist with ID " + invalidArtistId + " not found.", thrown2.getMessage());
    }

    @Test
    @DisplayName("deleteArtist: throws InvalidArgumentIdException when project or artist does not exist")
    void deleteArtist_WhenProjectOrArtistDoesNotExist_ThrowsException() {
        UUID invalidProjectId = randomUUID();
        UUID invalidArtistId = randomUUID();
        // Project not found
        when(jpaProjectRepository.findById(invalidProjectId)).thenReturn(Optional.empty());
        InvalidArgumentIdException thrown1 = assertThrows(
            InvalidArgumentIdException.class,
            () -> jpaProjectService.deleteArtist(invalidProjectId, invalidArtistId)
        );
        assertEquals("Project with ID " + invalidProjectId + " not found.", thrown1.getMessage());
        // Project found, artist not found
        when(jpaProjectRepository.findById(invalidProjectId)).thenReturn(Optional.of(project));
        when(artistService.findById(invalidArtistId)).thenReturn(Optional.empty());
        InvalidArgumentIdException thrown2 = assertThrows(
            InvalidArgumentIdException.class,
            () -> jpaProjectService.deleteArtist(invalidProjectId, invalidArtistId)
        );
        assertEquals("Artist with ID " + invalidArtistId + " not found.", thrown2.getMessage());
    }

    @Test
    @DisplayName("createFromDto: throws InvalidDTOFormat for invalid image DTO")
    void createFromDto_WithInvalidImageDto_ThrowsInvalidDTOFormat() {
        com.trails_art.trails.dtos.ImageDto badImageDto = new com.trails_art.trails.dtos.ImageDto("id", "image/png", "not_base64");
        com.trails_art.trails.dtos.ProjectImportDto badDto = new com.trails_art.trails.dtos.ProjectImportDto(
            "ProjectName", locationDto, badImageDto, "yt", artistData, false
        );
        try (MockedStatic<ProjectMapper> projectMapperMock = mockStatic(ProjectMapper.class)) {
            projectMapperMock.when(() -> ProjectMapper.mapToProject(badDto)).thenThrow(new com.trails_art.trails.exceptions.InvalidDTOFormat("Image DTO is not valid"));
            
            com.trails_art.trails.exceptions.InvalidDTOFormat thrown = assertThrows(
                com.trails_art.trails.exceptions.InvalidDTOFormat.class,
                () -> jpaProjectService.createFromDto(badDto)
            );
            assertEquals("Image DTO is not valid", thrown.getMessage());
        }
    }

    @Test
    @DisplayName("createFromDto: throws InvalidDTOFormat for invalid location DTO")
    void createFromDto_WithInvalidLocationDto_ThrowsInvalidDTOFormat() {
        com.trails_art.trails.dtos.LocationDto badLocationDto = new com.trails_art.trails.dtos.LocationDto("id", null, null);
        com.trails_art.trails.dtos.ProjectImportDto badDto = new com.trails_art.trails.dtos.ProjectImportDto(
            "ProjectName", badLocationDto, imageDto, "yt", artistData, false
        );
        try (MockedStatic<ProjectMapper> projectMapperMock = mockStatic(ProjectMapper.class)) {
            projectMapperMock.when(() -> ProjectMapper.mapToProject(badDto)).thenThrow(new com.trails_art.trails.exceptions.InvalidDTOFormat("Location DTO is not valid"));
            
            com.trails_art.trails.exceptions.InvalidDTOFormat thrown = assertThrows(
                com.trails_art.trails.exceptions.InvalidDTOFormat.class,
                () -> jpaProjectService.createFromDto(badDto)
            );
            assertEquals("Location DTO is not valid", thrown.getMessage());
        }
    }

    @Test
    @DisplayName("createFromDto: throws InvalidDTOFormat for invalid project DTO")
    void createFromDto_WithInvalidProjectDto_ThrowsInvalidDTOFormat() {
        com.trails_art.trails.dtos.ProjectImportDto badDto = new com.trails_art.trails.dtos.ProjectImportDto(
            null, locationDto, imageDto, "yt", artistData, false
        );
        try (MockedStatic<ProjectMapper> projectMapperMock = mockStatic(ProjectMapper.class)) {
            projectMapperMock.when(() -> ProjectMapper.mapToProject(badDto)).thenThrow(new com.trails_art.trails.exceptions.InvalidDTOFormat("Project DTO is not valid"));
            
            com.trails_art.trails.exceptions.InvalidDTOFormat thrown = assertThrows(
                com.trails_art.trails.exceptions.InvalidDTOFormat.class,
                () -> jpaProjectService.createFromDto(badDto)
            );
            assertEquals("Project DTO is not valid", thrown.getMessage());
        }
    }
}
