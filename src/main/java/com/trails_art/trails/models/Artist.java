package com.trails_art.trails.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name", "description", "instagramUrl"})
@Entity
@Table(name = "artists")
public class Artist {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Size(max = 255)
    @Column(name = "instagram_url")
    private String instagramUrl;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;

    @ManyToMany(mappedBy = "artists")
    private List<Project> projects = new ArrayList<>();

    public Artist(String name, Image image, String description, String instagramUrl) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.instagramUrl = instagramUrl;
    }
}
