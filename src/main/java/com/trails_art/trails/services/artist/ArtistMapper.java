package com.trails_art.trails.services.artist;

import com.trails_art.trails.dtos.ArtistImportDto;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.models.Project;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class ArtistMapper {

    public Artist mapToArtist(ArtistImportDto dto) {
        Image image = new Image(dto.image().mimetype(), Base64.getDecoder().decode(dto.image().data()));
        return new Artist(dto.name(), image, dto.description(), dto.instagram_url());
    }

    public Project mapToProject(ArtistImportDto.ProjectData dto) {
        Image image = new Image(dto.image().mimetype(), Base64.getDecoder().decode(dto.image().data()));
        Location location = new Location(dto.location().name(), dto.location().map_address());
        return new Project(dto.name(), location, image, dto.youtube_url());
    }
}

