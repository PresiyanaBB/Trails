package com.trails_art.trails.mappers;

import com.trails_art.trails.dtos.ProjectImportDto;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.models.Project;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class ProjectMapper {
    public Project mapToProject(ProjectImportDto dto) {
        Image image = new Image(dto.image().mimetype(), Base64.getDecoder().decode(dto.image().data()));
        Location location = new Location(dto.location().name(), dto.location().map_address());
        return new Project(dto.name(), location, image, dto.youtube_url());
    }

    public Artist mapToArtist(ProjectImportDto.ArtistData dto) {
        Image image = new Image(dto.image().mimetype(), Base64.getDecoder().decode(dto.image().data()));
        return new Artist(dto.name(), image, dto.description(), dto.instagram_url());
    }

}
