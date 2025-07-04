package com.trails_art.trails.services;

import com.trails_art.trails.dtos.EventDto;
import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.exceptions.InvalidDTOFormat;
import com.trails_art.trails.models.Event;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.repositories.JpaEventRepository;
import com.trails_art.trails.services.event.JpaEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventServiceTest {
    @Mock
    private JpaEventRepository jpaEventRepository;

    @InjectMocks
    private JpaEventService jpaEventService;

    // Common test data
    private ImageDto imageDto;
    private LocationDto locationDto;
    private EventDto eventDto;
    private Event event;
    private Image image;
    private Location location;
    private UUID eventId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        imageDto = createImageDto();
        locationDto = createLocationDto();
        eventDto = createEventDto();
        image = createImage();
        location = createLocation();
        event = createEvent();
        eventId = randomUUID();
    }

    // Helper methods
    private ImageDto createImageDto() {
        return new ImageDto(randomUUID().toString(), "image/png", Base64.getEncoder().encodeToString("data".getBytes()));
    }

    private LocationDto createLocationDto() {
        return new LocationDto(randomUUID().toString(), "LocName", "Address");
    }

    private EventDto createEventDto() {
        return new EventDto(randomUUID().toString(), "EventName", "desc", imageDto, LocalDateTime.now(), LocalDateTime.now().plusHours(2), locationDto);
    }

    private Event createEvent() {
        Event e = new Event();
        e.setId(eventId);
        e.setName("EventName");
        e.setDescription("desc");
        e.setImage(image);
        e.setStartTime(LocalDateTime.now());
        e.setEndTime(LocalDateTime.now().plusHours(2));
        e.setLocation(location);
        return e;
    }

    private Image createImage() {
        Image img = new Image();
        img.setId(randomUUID());
        img.setMimetype("image/png");
        img.setData("data".getBytes());
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
    @DisplayName("findAll: returns all events from repository")
    void findAll_WhenEventsExist_ReturnsAllEvents() {
        List<Event> events = Arrays.asList(new Event(), new Event());
        when(jpaEventRepository.findAll()).thenReturn(events);
        List<Event> result = jpaEventService.findAll();

        assertEquals(2, result.size());
        verify(jpaEventRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById: finds an event by its ID")
    void findById_WithValidId_ReturnsEvent() {
        when(jpaEventRepository.findById(eventId)).thenReturn(Optional.of(event));
        Optional<Event> result = jpaEventService.findById(eventId);

        assertTrue(result.isPresent());
        assertEquals(event, result.get());
        verify(jpaEventRepository, times(1)).findById(eventId);
    }

    @Test
    @DisplayName("create: saves event")
    void create_WithValidEvent_SavesEvent() {
        jpaEventService.create(event);
        verify(jpaEventRepository, times(1)).save(event);
    }

    @Test
    @DisplayName("createFromDto: creates event from valid DTO")
    void createFromDto_WithValidDto_CreatesEvent() {
        when(jpaEventRepository.save(any(Event.class))).thenReturn(new Event());
        assertDoesNotThrow(() -> jpaEventService.createFromDto(eventDto));
        verify(jpaEventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("createFromDto: throws InvalidDTOFormat for invalid DTO")
    void createFromDto_WithInvalidDto_ThrowsException() {
        EventDto invalidDto = new EventDto(null, null, null, null, null, null, null);
        assertThrows(InvalidDTOFormat.class, () -> jpaEventService.createFromDto(invalidDto));
    }

    @Test
    @DisplayName("update: updates an event when it exists")
    void update_WhenEventExists_UpdatesEvent() {
        when(jpaEventRepository.existsById(eventId)).thenReturn(true);
        jpaEventService.update(event, eventId);

        verify(jpaEventRepository, times(1)).save(event);
        assertEquals(eventId, event.getId());
    }

    @Test
    @DisplayName("update: throws InvalidArgumentIdException when event does not exist")
    void update_WhenEventDoesNotExist_ThrowsException() {
        when(jpaEventRepository.existsById(eventId)).thenReturn(false);
        assertThrows(InvalidArgumentIdException.class, () -> jpaEventService.update(event, eventId));
    }

    @Test
    @DisplayName("count: returns the count of events")
    void count_WhenCalled_ReturnsEventCount() {
        when(jpaEventRepository.count()).thenReturn(5L);
        int count = jpaEventService.count();

        assertEquals(5, count);
        verify(jpaEventRepository, times(1)).count();
    }

    @Test
    @DisplayName("saveAll: saves all events in a batch")
    void saveAll_WithEvents_SavesAllEvents() {
        List<Event> events = Arrays.asList(new Event(), new Event());
        jpaEventService.saveAll(events);
        verify(jpaEventRepository, times(1)).saveAll(events);
    }

    @Test
    @DisplayName("findByName: finds events by name (case-insensitive contains)")
    void findByName_WithName_ReturnsMatchingEvents() {
        List<Event> events = Arrays.asList(new Event(), new Event());
        when(jpaEventRepository.findByNameContainingIgnoreCase("event")).thenReturn(events);
        List<Event> result = jpaEventService.findByName("event");

        assertEquals(2, result.size());
        verify(jpaEventRepository, times(1)).findByNameContainingIgnoreCase("event");
    }
}
