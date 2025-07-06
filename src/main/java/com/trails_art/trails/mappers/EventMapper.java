package com.trails_art.trails.mappers;

import com.trails_art.trails.dtos.EventDto;
import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.models.Event;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;

public class EventMapper {
    public static Event mapToEvent(EventDto eventDto) {
        Image image = ImageMapper.mapToImage(eventDto.image());
        Location location = LocationMapper.mapToLocation(eventDto.location());

        return new Event(eventDto.name(), eventDto.description(),
                image, eventDto.start_time(), eventDto.end_time(), location);
    }

    public static EventDto mapToEventDto(Event event) {
        ImageDto imageDto = ImageMapper.mapToImageDto(event.getImage());
        LocationDto locationDto = LocationMapper.mapToLocationDto(event.getLocation());

        return new EventDto(
                event.getId().toString(),
                event.getName(),
                event.getDescription(),
                imageDto,
                event.getStartTime(),
                event.getEndTime(),
                locationDto
        );
    }

}
