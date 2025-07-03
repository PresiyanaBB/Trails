package com.trails_art.trails.services;

import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.repositories.JpaLocationRepository;
import com.trails_art.trails.services.location.JpaLocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {
    @Mock
    private JpaLocationRepository jpaLocationRepository;
    @InjectMocks
    private JpaLocationService jpaLocationService;

    private UUID locationId;
    private Location location;
    private LocationDto locationDto;

    @BeforeEach
    void setUp() {
        locationId = UUID.randomUUID();
        location = createLocation();
        locationDto = createLocationDto();
    }

    private Location createLocation() {
        Location loc = new Location("LocName", "LocAddr");
        loc.setId(locationId);
        return loc;
    }

    private LocationDto createLocationDto() {
        return new LocationDto(locationId.toString(), "LocName", "LocAddr");
    }

    @Test
    @DisplayName("findAll: returns all locations from repository")
    void findAll_WhenLocationsExist_ReturnsAllLocations() {
        List<Location> locations = Arrays.asList(new Location(), new Location());
        when(jpaLocationRepository.findAll()).thenReturn(locations);
        List<Location> result = jpaLocationService.findAll();

        assertEquals(2, result.size());
        verify(jpaLocationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById: finds a location by its ID")
    void findById_WithValidId_ReturnsLocation() {
        when(jpaLocationRepository.findById(locationId)).thenReturn(Optional.of(location));
        Optional<Location> result = jpaLocationService.findById(locationId);

        assertTrue(result.isPresent());
        assertEquals(location, result.get());
        verify(jpaLocationRepository, times(1)).findById(locationId);
    }

    @Test
    @DisplayName("create: saves location")
    void create_WithValidLocation_SavesLocation() {
        jpaLocationService.create(location);
        verify(jpaLocationRepository, times(1)).save(location);
    }

    @Test
    @DisplayName("createFromDto: creates location from DTO")
    void createFromDto_WithValidDto_CreatesLocation() {
        jpaLocationService.createFromDto(locationDto);
        verify(jpaLocationRepository, times(1)).save(any(Location.class));
    }

    @Test
    @DisplayName("update: updates location when it exists")
    void update_WhenLocationExists_UpdatesLocation() {
        when(jpaLocationRepository.existsById(locationId)).thenReturn(true);
        jpaLocationService.update(location, locationId);

        verify(jpaLocationRepository, times(1)).save(location);
        assertEquals(locationId, location.getId());
    }

    @Test
    @DisplayName("update: throws when location id not found")
    void update_WhenLocationIdNotFound_ThrowsException() {
        when(jpaLocationRepository.existsById(locationId)).thenReturn(false);
        InvalidArgumentIdException thrown = assertThrows(
                InvalidArgumentIdException.class,
                () -> jpaLocationService.update(location, locationId)
        );
        assertEquals("Location with ID " + locationId + " not found.", thrown.getMessage());
        verify(jpaLocationRepository, times(1)).existsById(locationId);
        verify(jpaLocationRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateFromDto: updates location from DTO when location exists")
    void updateFromDto_WithValidDto_UpdatesLocation() {
        when(jpaLocationRepository.findById(locationId)).thenReturn(Optional.of(location));
        when(jpaLocationRepository.existsById(locationId)).thenReturn(true);
        LocationDto dto = createLocationDto();
        jpaLocationService.updateFromDto(dto, locationId);
        verify(jpaLocationRepository, times(1)).save(location);
        assertEquals("LocName", location.getName());
        assertEquals("LocAddr", location.getMapAddress());
    }

    @Test
    @DisplayName("updateFromDto: throws when location id not found")
    void updateFromDto_WhenLocationIdNotFound_ThrowsException() {
        when(jpaLocationRepository.findById(locationId)).thenReturn(Optional.empty());
        LocationDto dto = createLocationDto();
        InvalidArgumentIdException thrown = assertThrows(
                InvalidArgumentIdException.class,
                () -> jpaLocationService.updateFromDto(dto, locationId)
        );
        assertEquals("Location with ID " + locationId + " not found.", thrown.getMessage());
        verify(jpaLocationRepository, times(1)).findById(locationId);
        verify(jpaLocationRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete: deletes location by ID")
    void delete_WhenCalled_DeletesLocation() {
        jpaLocationService.delete(locationId);
        verify(jpaLocationRepository, times(1)).deleteById(locationId);
    }

    @Test
    @DisplayName("count: returns the count of locations")
    void count_WhenCalled_ReturnsLocationCount() {
        when(jpaLocationRepository.count()).thenReturn(4L);
        int count = jpaLocationService.count();
        assertEquals(4, count);
        verify(jpaLocationRepository, times(1)).count();
    }

    @Test
    @DisplayName("saveAll: saves all locations in a batch")
    void saveAll_WithLocations_SavesAllLocations() {
        List<Location> locations = Arrays.asList(new Location(), new Location());
        jpaLocationService.saveAll(locations);
        verify(jpaLocationRepository, times(1)).saveAll(locations);
    }

    @Test
    @DisplayName("findByName: finds locations by name (case-insensitive contains)")
    void findByName_WithName_ReturnsMatchingLocations() {
        String name = "test";
        List<Location> locations = Arrays.asList(new Location(), new Location());
        when(jpaLocationRepository.findByNameContainingIgnoreCase(name)).thenReturn(locations);
        List<Location> result = jpaLocationService.findByName(name);
        assertEquals(2, result.size());
        verify(jpaLocationRepository).findByNameContainingIgnoreCase(name);
    }
}
