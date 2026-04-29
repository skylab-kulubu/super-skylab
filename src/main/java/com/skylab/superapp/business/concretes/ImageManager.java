package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.core.config.r2.FolderType;
import com.skylab.superapp.core.constants.ImageMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.mappers.ImageMapper;
import com.skylab.superapp.core.utilities.storage.R2StorageService;
import com.skylab.superapp.dataAccess.ImageDao;
import com.skylab.superapp.entities.DTOs.Image.response.UploadImageResponseDto;
import com.skylab.superapp.entities.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.Imaging;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageManager implements ImageService {

    private final ImageDao imageDao;
    private final R2StorageService r2StorageService;
    private final ImageMapper imageMapper;

    @Override
    public Image uploadImage(MultipartFile image) {
        log.info("Initiating image upload. FileName: {}, FileSize: {}", image.getOriginalFilename(), image.getSize());

        if (image.isEmpty()) {
            log.warn("Image upload failed: File is empty. FileName: {}", image.getOriginalFilename());
            throw new ValidationException(ImageMessages.IMAGE_CANNOT_BE_NULL);
        }

        if (image.getSize() > 10 * 1024 * 1024) {
            log.warn("Image upload failed: File size exceeds 10MB limit. FileName: {}, FileSize: {}", image.getOriginalFilename(), image.getSize());
            throw new ValidationException(ImageMessages.IMAGE_SIZE_ERROR);
        }

        try {
            log.debug("Processing image sanitization to remove metadata. FileName: {}", image.getOriginalFilename());
            byte[] cleanImageBytes = removeMetadata(image);

            String imageKey = r2StorageService.uploadFile(
                    cleanImageBytes,
                    image.getOriginalFilename(),
                    image.getContentType(),
                    FolderType.IMAGE
            );

            Image newImage = new Image();
            newImage.setFileName(image.getOriginalFilename());
            newImage.setFileType(image.getContentType());
            newImage.setFileUrl(imageKey);
            newImage.setFileSize((long) cleanImageBytes.length);

            var savedImage = imageDao.save(newImage);
            log.info("Image uploaded and saved successfully. ImageId: {}, FileName: {}", savedImage.getId(), savedImage.getFileName());

            return savedImage;
        } catch (Exception e) {
            log.error("Image upload failed: Unexpected error during processing. FileName: {}, ErrorMessage: {}", image.getOriginalFilename(), e.getMessage(), e);
            throw new RuntimeException(ImageMessages.IMAGE_UPLOAD_ERROR);
        }
    }

    @Override
    public UploadImageResponseDto uploadImageDto(MultipartFile image) {
        Image savedImage = uploadImage(image);
        return imageMapper.toUploadImageResponseDto(savedImage);
    }

    @Override
    public void deleteImage(UUID imageId) {
        log.warn("Delete image operation invoked but not implemented. ImageId: {}", imageId);
    }

    @Override
    public List<Image> getImagesByIds(List<UUID> imageIds) {
        log.debug("Retrieving multiple images. RequestedCount: {}", imageIds != null ? imageIds.size() : 0);

        if (imageIds == null || imageIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Image> images = imageDao.findAllById(imageIds);

        Set<UUID> foundIds = images.stream()
                .map(Image::getId)
                .collect(Collectors.toSet());

        List<UUID> missing = imageIds.stream()
                .filter(id -> !foundIds.contains(id))
                .distinct()
                .toList();

        if (!missing.isEmpty()) {
            log.error("Image batch retrieval failed: Some resources not found. MissingIds: {}", missing);
            throw new ResourceNotFoundException("Images not found for ids: " + missing);
        }

        return images;
    }

    @Override
    public Image getImageEntityById(UUID coverImageId) {
        log.debug("Retrieving image entity. ImageId: {}", coverImageId);

        return imageDao.findById(coverImageId)
                .orElseThrow(() -> {
                    log.error("Image entity retrieval failed: Resource not found. ImageId: {}", coverImageId);
                    return new ResourceNotFoundException("Image not found with id: " + coverImageId);
                });
    }

    private byte[] removeMetadata(MultipartFile image) throws IOException {
        String extension = getFileExtension(image.getOriginalFilename()).toLowerCase().substring(1);

        log.debug("Stripping metadata from image. Extension: {}", extension);

        BufferedImage bufferedImage = Imaging.getBufferedImage(image.getInputStream());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, extension, outputStream);

        return outputStream.toByteArray();
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            log.warn("File extension validation failed: No extension found. FileName: {}", fileName);
            throw new IllegalArgumentException("File has no extension: " + fileName);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}