package com.trails_art.trails.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue
    private UUID id;

    @Size(max = 255)
    @Column(name = "mimetype")
    private String mimetype;

//    @Column(name = "data", nullable = false, columnDefinition = "BYTEA")
    @Column(name = "data", nullable = false)
    private byte[] data;

    public Image(String mimetype, byte[] bytes) {
        this.mimetype = mimetype;
        this.data = bytes;
    }

    public Image() {

    }
}