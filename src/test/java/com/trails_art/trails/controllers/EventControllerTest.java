package com.trails_art.trails.controllers;

import com.trails_art.trails.dtos.EventDto;
import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.models.Event;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.services.event.JpaEventService;
import com.trails_art.trails.services.image.JpaImageService;
import com.trails_art.trails.services.location.JpaLocationService;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventControllerTest {

    private final JpaEventService jpaEventService = mock(JpaEventService.class);
    private final JpaImageService jpaImageService = mock(JpaImageService.class);
    private final JpaLocationService jpaLocationService = mock(JpaLocationService.class);
    private final EventController eventController = new EventController(jpaEventService, jpaImageService, jpaLocationService);

    @Test
    void testFindAll() {
        Event mockEvent = new Event("Event Name", "Event Description",
                new Image("image/png", new byte[0]), LocalDateTime.now(), LocalDateTime.now(),
                new Location("Location Name", "Map Address"));
        when(jpaEventService.findAll()).thenReturn(List.of(mockEvent));

        List<EventDto> result = eventController.findAll();
        assertEquals(1, result.size());
        verify(jpaEventService, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        UUID id = UUID.randomUUID();
        Event mockEvent = new Event("Event Name", "Event Description",
                new Image("image/png", new byte[0]), LocalDateTime.now(), LocalDateTime.now(),
                new Location("Location Name", "Map Address"));
        when(jpaEventService.findById(id)).thenReturn(Optional.of(mockEvent));

        EventDto result = eventController.findById(id);
        assertEquals("Event Name", result.name());
        verify(jpaEventService, times(1)).findById(id);
    }

    @Test
    void testFindById_NotFound() {
        UUID id = UUID.randomUUID();
        when(jpaEventService.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            eventController.findById(id);
        });
        assertEquals("Event not found.", exception.getReason());
        verify(jpaEventService, times(1)).findById(id);
    }

    @Test
    void testCreate() {
        EventDto eventDto = new EventDto(
                "Event Name", "Event Description",
                new ImageDto("image/png", Base64.getEncoder().encodeToString(new byte[0])),
                LocalDateTime.now(), LocalDateTime.now(),
                new LocationDto("Location Name", "Map Address"));

        eventController.create(eventDto);
        verify(jpaImageService, times(1)).create(any(Image.class));
        verify(jpaLocationService, times(1)).create(any(Location.class));
        verify(jpaEventService, times(1)).create(any(Event.class));
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();

        eventController.delete(id.toString());
        verify(jpaEventService, times(1)).delete(id);
    }

    @Test
    void testCount() {
        when(jpaEventService.count()).thenReturn(3);

        int result = eventController.count();
        assertEquals(3, result);
        verify(jpaEventService, times(1)).count();
    }

    @Test
    void testFindByName() {
        String name = "Event Name";
        Event mockEvent = new Event(name, "Event Description",
                new Image("image/png", new byte[0]), LocalDateTime.now(), LocalDateTime.now(),
                new Location("Location Name", "Map Address"));
        when(jpaEventService.findByName(name)).thenReturn(List.of(mockEvent));

        List<EventDto> result = eventController.findByName(name);
        assertEquals(1, result.size());
        assertEquals(name, result.get(0).name());
        verify(jpaEventService, times(1)).findByName(name);
    }
}
