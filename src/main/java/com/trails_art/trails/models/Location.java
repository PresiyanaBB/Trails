package com.trails_art.trails.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue
    private UUID id;

    @Size(max = 255)
    @Column(name = "map_address")
    private String mapAddress;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    public Location(String name, String mapAddress) {
        this.name = name;
        this.mapAddress = mapAddress;
    }

    public Location() {

    }
}