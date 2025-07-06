package com.trails_art.trails.mappers;

import com.trails_art.trails.dtos.ArtistDataDto;
import com.trails_art.trails.dtos.ArtistExportDto;
import com.trails_art.trails.dtos.ArtistImportDto;
import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.dtos.ProjectDataDto;
import com.trails_art.trails.exceptions.InvalidDTOFormat;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Image;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArtistMapper {

    public static Artist mapToArtist(ArtistDataDto dto) {
        Image image = ImageMapper.mapToImage(dto.image());

        Artist artist;
        try {
            artist = new Artist(dto.name(), image, dto.description(), dto.instagram_url());
        } catch (Exception e) {
            throw new InvalidDTOFormat("Artist DTO is not valid");
        }

        return artist;
    }

    public static Artist mapToArtist(ArtistImportDto dto) {
        Image image = ImageMapper.mapToImage(dto.image());

        Artist artist;
        try {
            artist = new Artist(dto.name(), image, dto.description(), dto.instagram_url());
        } catch (Exception e) {
            throw new InvalidDTOFormat("Artist DTO is not valid");
        }

        return artist;
    }

    public static ArtistExportDto mapToArtistDto(Artist artist) {
        ImageDto imageDto = ImageMapper.mapToImageDto(artist.getImage());

        List<ProjectDataDto> projectDataList = artist.getProjects().stream()
                .map(ap -> new ProjectDataDto(
                        ap.getName(),
                        LocationMapper.mapToLocationDto(ap.getLocation()),
                        ImageMapper.mapToImageDto(ap.getImage()),
                        ap.getYoutubeUrl(),
                        ap.getCreatedOn().toString()
                )).toList();

        return new ArtistExportDto(
                artist.getId().toString(),
                artist.getName(),
                imageDto,
                artist.getDescription(),
                artist.getInstagramUrl(),
                projectDataList
        );
    }

}

