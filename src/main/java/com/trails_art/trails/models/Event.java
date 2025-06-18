package com.trails_art.trails.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "events", indexes = {
        @Index(name = "idx_event_start_time", columnList = "start_time")
})
public class Event {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "location_id")
    private Location location;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    public Event(String name, String description, Image image, LocalDateTime startTime, LocalDateTime endTime, Location location) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    public Event() {

    }
}