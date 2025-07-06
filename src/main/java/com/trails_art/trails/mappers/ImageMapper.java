package com.trails_art.trails.mappers;

import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.exceptions.InvalidDTOFormat;
import com.trails_art.trails.models.Image;

import java.util.Base64;

public class ImageMapper {
    public static Image mapToImage(ImageDto dto) {
        Image image;
        try {
            image = new Image(dto.mimetype(), Base64.getDecoder().decode(dto.data()));
        } catch (Exception e) {
            throw new InvalidDTOFormat("Image DTO is not valid");
        }

        return image;
    }

    public static ImageDto mapToImageDto(Image image) {
        return new ImageDto(
                image.getId().toString(),
                image.getMimetype(),
                Base64.getEncoder().encodeToString(image.getData())
        );
    }
}
