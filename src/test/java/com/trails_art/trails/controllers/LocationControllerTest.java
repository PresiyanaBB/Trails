package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.services.location.JpaLocationService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocationControllerTest {

    private final JpaLocationService jpaLocationService = mock(JpaLocationService.class);
    private final LocationController locationController = new LocationController(jpaLocationService);

    @Test
    void testFindAll() {
        List<Location> mockLocations = List.of(new Location("Park", "123 Map Street"));
        when(jpaLocationService.findAll()).thenReturn(mockLocations);

        List<Location> result = locationController.findAll();
        assertEquals(mockLocations, result);
        verify(jpaLocationService, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        UUID id = UUID.randomUUID();
        Location mockLocation = new Location("Park", "123 Map Street");
        when(jpaLocationService.findById(id)).thenReturn(Optional.of(mockLocation));

        Location result = locationController.findById(id);
        assertEquals(mockLocation, result);
        verify(jpaLocationService, times(1)).findById(id);
    }

    @Test
    void testFindById_NotFound() {
        UUID id = UUID.randomUUID();
        when(jpaLocationService.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            locationController.findById(id);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Location not found.", exception.getReason());
    }

    @Test
    void testCreate() {
        LocationDto locationDto = new LocationDto("Park", "123 Map Street");
        Location location = new Location(locationDto.name(), locationDto.map_address());

        locationController.create(locationDto);
        verify(jpaLocationService, times(1)).create(refEq(location));
    }

    @Test
    void testUpdate() {
        UUID id = UUID.randomUUID();
        LocationDto locationDto = new LocationDto("Updated Park", "456 New Street");
        Location existingLocation = new Location("Old Park", "123 Map Street");
        when(jpaLocationService.findById(id)).thenReturn(Optional.of(existingLocation));

        locationController.update(locationDto, id);
        assertEquals("Updated Park", existingLocation.getName());
        assertEquals("456 New Street", existingLocation.getMapAddress());
        verify(jpaLocationService, times(1)).update(existingLocation, id);
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();

        locationController.delete(id.toString());
        verify(jpaLocationService, times(1)).delete(id);
    }

    @Test
    void testCount() {
        when(jpaLocationService.count()).thenReturn(5);

        int result = locationController.count();
        assertEquals(5, result);
        verify(jpaLocationService, times(1)).count();
    }

    @Test
    void testFindByName() {
        String name = "Park";
        List<Location> mockLocations = List.of(new Location("Park", "123 Map Street"));
        when(jpaLocationService.findByName(name)).thenReturn(mockLocations);

        List<Location> result = locationController.findByName(name);
        assertEquals(mockLocations, result);
        verify(jpaLocationService, times(1)).findByName(name);
    }
}
