package com.trails_art.trails.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"mimetype", "data"})
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "mimetype", nullable = false)
    private String mimetype;

    //@Lob
    //BYTEA
    @Column(name = "data", nullable = false)
    private byte[] data;

    public Image(
            String mimetype,
            byte[] bytes
    ) {
        this.mimetype = mimetype;
        this.data = bytes;
    }
}