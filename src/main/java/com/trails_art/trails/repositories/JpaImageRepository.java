package com.trails_art.trails.repositories;

import com.trails_art.trails.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JpaImageRepository extends JpaRepository<Image, UUID> {

}
