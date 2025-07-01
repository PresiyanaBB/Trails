package com.trails_art.trails.mappers;

import com.trails_art.trails.dtos.ArtistImportDto;
import com.trails_art.trails.exceptions.InvalidDTOFormat;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.models.Project;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class ArtistMapper {

    public Artist mapToArtist(ArtistImportDto dto) {
        Image image;
        try {
            image = new Image(dto.image().mimetype(), Base64.getDecoder().decode(dto.image().data()));
        } catch (Exception e) {
            throw new InvalidDTOFormat("Image DTO is not valid");
        }

        Artist artist;
        try {
            artist = new Artist(dto.name(), image, dto.description(), dto.instagram_url());
        } catch (Exception e) {
            throw new InvalidDTOFormat("Artist DTO is not valid");
        }

        return artist;
    }

    public Project mapToProject(ArtistImportDto.ProjectData dto) {
        Image image;
        try {
            image = new Image(dto.image().mimetype(), Base64.getDecoder().decode(dto.image().data()));
        } catch (Exception e) {
            throw new InvalidDTOFormat("Image DTO is not valid");
        }

        Location location;
        try {
         location = new Location(dto.location().name(), dto.location().map_address());
        } catch (Exception e) {
            throw new InvalidDTOFormat("Location DTO is not valid");
        }

        Project project;
        try {
            project = new Project(dto.name(), location, image, dto.youtube_url());
        } catch (Exception e) {
            throw new InvalidDTOFormat("Project DTO is not valid");
        }

        return project;
    }
}

