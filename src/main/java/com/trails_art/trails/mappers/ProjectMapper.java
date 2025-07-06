package com.trails_art.trails.mappers;

import com.trails_art.trails.dtos.ArtistDataDto;
import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.dtos.ProjectDataDto;
import com.trails_art.trails.dtos.ProjectExportDto;
import com.trails_art.trails.dtos.ProjectImportDto;
import com.trails_art.trails.exceptions.InvalidDTOFormat;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.models.Location;
import com.trails_art.trails.models.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectMapper {
    public static Project mapToProject(ProjectDataDto dto) {
        Image image = ImageMapper.mapToImage(dto.image());

        Location location = LocationMapper.mapToLocation(dto.location());

        Project project;
        try {
            project = new Project(dto.name(), location, image, dto.youtube_url());
        } catch (Exception e) {
            throw new InvalidDTOFormat("Project DTO is not valid");
        }

        return project;
    }

    public static Project mapToProject(ProjectImportDto dto) {
        Image image = ImageMapper.mapToImage(dto.image());

        Location location = LocationMapper.mapToLocation(dto.location());

        Project project;
        try {
            project = new Project(dto.name(), location, image, dto.youtube_url());
        } catch (Exception e) {
            throw new InvalidDTOFormat("Project DTO is not valid");
        }

        return project;
    }

    public static ProjectExportDto mapToProjectDto(Project project) {
        ImageDto imageDto = ImageMapper.mapToImageDto(project.getImage());

        LocationDto locationDto = LocationMapper.mapToLocationDto(project.getLocation());

        List<ArtistDataDto> artistDataList = project.getArtists().stream()
                .map(ap -> new ArtistDataDto(
                        ap.getName(),
                        ImageMapper.mapToImageDto(ap.getImage()),
                        ap.getDescription(),
                        ap.getInstagramUrl()
                )).toList();

        return new ProjectExportDto(
                project.getId().toString(),
                project.getName(),
                locationDto,
                imageDto,
                project.getYoutubeUrl(),
                project.getCreatedOn(),
                artistDataList
        );
    }
}
