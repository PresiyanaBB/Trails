package com.trails_art.trails.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name", "mapAddress"})
@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "map_address")
    private String mapAddress;

    public Location(
            String name,
            String mapAddress)
    {
        this.name = name;
        this.mapAddress = mapAddress;
    }
}
