package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.config.r2.FolderType;
import com.skylab.superapp.core.constants.ImageMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.mappers.ImageMapper;
import com.skylab.superapp.core.utilities.storage.R2StorageService;
import com.skylab.superapp.dataAccess.ImageDao;
import com.skylab.superapp.entities.DTOs.Image.response.UploadImageResponseDto;
import com.skylab.superapp.entities.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class ImageManager implements ImageService {

    private final ImageDao imageDao;
    private final R2StorageService r2StorageService;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(ImageManager.class);
    private final ImageMapper imageMapper;

    public ImageManager(ImageDao imageDao, R2StorageService r2StorageService, UserService userService, ImageMapper imageMapper) {
        this.imageDao = imageDao;
        this.r2StorageService = r2StorageService;
        this.userService = userService;
        this.imageMapper = imageMapper;
    }

    @Override
    public Image uploadImage(MultipartFile image) {

        logger.info("Uploading image file: {}", image.getOriginalFilename());
        if (image.isEmpty()){
            logger.error("Image upload failed: Image file is empty.");
            throw new ValidationException(ImageMessages.IMAGE_CANNOT_BE_NULL);
        }

        if (image.getSize() > 10 * 1024 * 1024) {
            logger.error("Image upload failed: Image file size exceeds the limit of 10 MB.");
            throw new ValidationException(ImageMessages.IMAGE_SIZE_ERROR);
        }

        try {
            byte[] cleanImageBytes = removeMetadata(image);

            String imageKey = r2StorageService.uploadFile(
                    cleanImageBytes,
                    image.getOriginalFilename(),
                    image.getContentType(),
                    FolderType.IMAGE);


            Image newImage = new Image();
            newImage.setFileName(image.getOriginalFilename());
            newImage.setFileType(image.getContentType());
            newImage.setFileUrl(imageKey);
            newImage.setFileSize((long) cleanImageBytes.length);

            var savedImage = imageDao.save(newImage);
            logger.info("Image uploaded successfully: {}", savedImage.getFileName());
            return savedImage;
        }catch (Exception e) {
            logger.error("Image upload failed fileName: {}, errorMessage: {}", image.getOriginalFilename(), e.getMessage());
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

    }

    @Override
    public List<Image> getImagesByIds(List<UUID> imageIds) {
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
            throw new ResourceNotFoundException("Images not found for ids: " + missing);
        }

        return images;
    }


    private byte[] removeMetadata(MultipartFile image) throws IOException {
        BufferedImage originalImage = ImageIO.read(image.getInputStream());

        if (originalImage == null){
            throw new IOException("Image file is empty.");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String formatName = getFileExtension(image.getOriginalFilename()).substring(1);


        ImageIO.write(originalImage, formatName, outputStream);

        return outputStream.toByteArray();
    }

    private String getFileExtension(String fileName) {

        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("File has no extension: " + fileName);
        }

        return fileName.substring(fileName.lastIndexOf("."));

    }


}