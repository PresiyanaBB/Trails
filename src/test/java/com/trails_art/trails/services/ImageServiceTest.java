package com.trails_art.trails.services;

import com.trails_art.trails.dtos.ImageDto;
import com.trails_art.trails.exceptions.InvalidArgumentIdException;
import com.trails_art.trails.models.Image;
import com.trails_art.trails.repositories.JpaImageRepository;
import com.trails_art.trails.services.image.JpaImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {
    @Mock
    private JpaImageRepository jpaImageRepository;
    @InjectMocks
    private JpaImageService jpaImageService;

    private UUID imageId;
    private Image image;
    private ImageDto imageDto;

    @BeforeEach
    void setUp() {
        imageId = UUID.randomUUID();
        image = createImage();
        imageDto = createImageDto();
    }

    private Image createImage() {
        Image img = new Image("image/png", "data".getBytes());
        img.setId(imageId);
        return img;
    }

    private ImageDto createImageDto() {
        return new ImageDto(imageId.toString(), "image/png", Base64.getEncoder().encodeToString("data".getBytes()));
    }

    @Test
    @DisplayName("findAll: returns all images from repository")
    void findAll_WhenImagesExist_ReturnsAllImages() {
        List<Image> images = Arrays.asList(new Image(), new Image());

        when(jpaImageRepository.findAll()).thenReturn(images);
        List<Image> result = jpaImageService.findAll();

        assertEquals(2, result.size());
        verify(jpaImageRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById: finds an image by its ID")
    void findById_WithValidId_ReturnsImage() {
        when(jpaImageRepository.findById(imageId)).thenReturn(Optional.of(image));
        Optional<Image> result = jpaImageService.findById(imageId);

        assertTrue(result.isPresent());
        assertEquals(image, result.get());
        verify(jpaImageRepository, times(1)).findById(imageId);
    }

    @Test
    @DisplayName("create: saves image")
    void create_WithValidImage_SavesImage() {
        jpaImageService.create(image);
        verify(jpaImageRepository, times(1)).save(image);
    }

    @Test
    @DisplayName("createFromDto: creates image from DTO")
    void createFromDto_WithValidDto_CreatesImage() {
        jpaImageService.createFromDto(imageDto);
        verify(jpaImageRepository, times(1)).save(any(Image.class));
    }

    @Test
    @DisplayName("update: updates image when it exists")
    void update_WhenImageExists_UpdatesImage() {
        when(jpaImageRepository.existsById(imageId)).thenReturn(true);
        jpaImageService.update(image, imageId);

        verify(jpaImageRepository, times(1)).save(image);
        assertEquals(imageId, image.getId());
    }

    @Test
    @DisplayName("update: throws when image id not found")
    void update_WhenImageIdNotFound_ThrowsException() {
        when(jpaImageRepository.existsById(imageId)).thenReturn(false);
        InvalidArgumentIdException thrown = assertThrows(
                InvalidArgumentIdException.class,
                () -> jpaImageService.update(image, imageId)
        );
        assertEquals("Image with ID " + imageId + " not found.", thrown.getMessage());
        verify(jpaImageRepository, times(1)).existsById(imageId);
        verify(jpaImageRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateFromDto: updates image from DTO when image exists")
    void updateFromDto_WithValidDto_UpdatesImage() {
        when(jpaImageRepository.findById(imageId)).thenReturn(Optional.of(image));
        when(jpaImageRepository.existsById(imageId)).thenReturn(true);
        ImageDto dto = createImageDto();
        jpaImageService.updateFromDto(dto, imageId);

        verify(jpaImageRepository, times(1)).save(image);
        assertEquals("image/png", image.getMimetype());
        assertArrayEquals(Base64.getDecoder().decode(dto.data()), image.getData());
    }

    @Test
    @DisplayName("updateFromDto: throws when image id not found")
    void updateFromDto_WhenImageIdNotFound_ThrowsException() {
        when(jpaImageRepository.findById(imageId)).thenReturn(Optional.empty());
        ImageDto dto = createImageDto();
        InvalidArgumentIdException thrown = assertThrows(
                InvalidArgumentIdException.class,
                () -> jpaImageService.updateFromDto(dto, imageId)
        );

        assertEquals("Image with ID " + imageId + " not found.", thrown.getMessage());
        verify(jpaImageRepository, times(1)).findById(imageId);
        verify(jpaImageRepository, never()).save(any());
    }

    @Test
    @DisplayName("count: returns the count of images")
    void count_WhenCalled_ReturnsImageCount() {
        when(jpaImageRepository.count()).thenReturn(3L);
        int count = jpaImageService.count();
        assertEquals(3, count);
        verify(jpaImageRepository, times(1)).count();
    }

    @Test
    @DisplayName("saveAll: saves all images in a batch")
    void saveAll_WithImages_SavesAllImages() {
        List<Image> images = Arrays.asList(new Image(), new Image());
        jpaImageService.saveAll(images);
        verify(jpaImageRepository, times(1)).saveAll(images);
    }
}
