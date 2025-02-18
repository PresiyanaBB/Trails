package com.trails_art.trails.modules;
import java.util.UUID;

import jakarta.persistence.*;

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
