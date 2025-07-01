package com.trails_art.trails.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Size(max = 255)
    @Column(name = "instagram_url")
    private String instagramUrl;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "artists")
    private List<Project> projects = new ArrayList<>();


    public Artist(String name, Image image, String description, String instagramUrl) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.instagramUrl = instagramUrl;
    }

    public Artist() {

    }
}