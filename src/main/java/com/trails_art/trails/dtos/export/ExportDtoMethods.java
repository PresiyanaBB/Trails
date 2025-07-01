package com.trails_art.trails.dtos.export;

import com.trails_art.trails.dtos.ArtistExportDto;
import com.trails_art.trails.dtos.EventDto;
import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.dtos.LocationDto;
import com.trails_art.trails.dtos.ProjectExportDto;
import com.trails_art.trails.models.Artist;
import com.trails_art.trails.models.Event;
import com.trails_art.trails.models.Project;

import java.util.Base64;
import java.util.List;

public class ExportDtoMethods {
    public static ProjectExportDto exportProject(Project project) {
        ImageDto imageDto = new ImageDto(
                project.getImage().getId().toString(),
                project.getImage().getMimetype(),
                Base64.getEncoder().encodeToString(project.getImage().getData())
        );

        LocationDto locationDto = new LocationDto(
                project.getLocation().getId().toString(),
                project.getLocation().getName(),
                project.getLocation().getMapAddress()
        );

        List<ProjectExportDto.ArtistData> artistDataList = project.getArtists().stream()
                .map(ap -> new ProjectExportDto.ArtistData(
                        ap.getName(),
                        new ImageDto(
                                ap.getImage().getId().toString(),
                                ap.getImage().getMimetype(),
                                Base64.getEncoder().encodeToString(ap.getImage().getData())
                        ),
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

    public static ArtistExportDto exportArtist(Artist artist) {
        ImageDto imageDto = new ImageDto(
                artist.getId().toString(),
                artist.getImage().getMimetype(),
                Base64.getEncoder().encodeToString(artist.getImage().getData())
        );

        List<ArtistExportDto.ProjectData> projectDataList = artist.getProjects().stream()
                .map(ap -> new ArtistExportDto.ProjectData(
                        ap.getName(),
                        new LocationDto(
                                ap.getLocation().getId().toString(),
                                ap.getLocation().getName(),
                                ap.getLocation().getMapAddress()
                        ),
                        new ImageDto(
                                ap.getImage().getId().toString(),
                                ap.getImage().getMimetype(),
                                Base64.getEncoder().encodeToString(ap.getImage().getData())
                        ),
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

    public static EventDto eventExport(Event event) {
        ImageDto imageDto = new ImageDto(
                event.getImage().getId().toString(),
                event.getImage().getMimetype(),
                Base64.getEncoder().encodeToString(event.getImage().getData())
        );

        LocationDto locationDto = new LocationDto(
                event.getLocation().getId().toString(),
                event.getLocation().getName(),
                event.getLocation().getMapAddress()
        );

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
