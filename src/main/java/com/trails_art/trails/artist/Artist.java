package com.trails_art.trails.artist;
import java.util.UUID;

import com.trails_art.trails.project.Project;
import jakarta.persistence.*;
import com.trails_art.trails.image.Image;
import java.util.List;
import java.util.ArrayList;


@Entity
public record Artist(
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        UUID id,
        String name,
        @OneToOne(cascade = CascadeType.ALL)
        Image image,
        String description,
        String instagramUrl,
        @ManyToMany
        @JoinTable(
                name = "artist_project",
                joinColumns = @JoinColumn(name = "artist_id"),
                inverseJoinColumns = @JoinColumn(name = "project_id")
        )
        List<Project> projects
) {
        public Artist {
                projects = new ArrayList<>();
        }
}
