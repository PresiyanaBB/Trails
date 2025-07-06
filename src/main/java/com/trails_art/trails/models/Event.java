package com.trails_art.trails.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name", "startTime", "endTime"})
@Entity
@Table(name = "events", indexes = {
        @Index(name = "idx_event_start_time", columnList = "start_time")
})
public class Event {

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

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "location_id")
    private Location location;

    public Event(String name,
                 String description,
                 Image image,
                 LocalDateTime startTime,
                 LocalDateTime endTime,
                 Location location) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }
}
