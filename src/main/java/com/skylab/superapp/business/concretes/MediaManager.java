package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.FileService;
import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.business.abstracts.MediaService;
import com.skylab.superapp.core.mappers.MediaMapper;
import com.skylab.superapp.dataAccess.MediaDao;
import com.skylab.superapp.entities.DTOs.media.response.MediaUploadResponseDto;
import com.skylab.superapp.entities.Media;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class MediaManager implements MediaService {

    private final Logger logger = LoggerFactory.getLogger(MediaManager.class);

    private final MediaDao mediaDao;

    private final ImageService imageService;

    private final FileService fileService;

    private final MediaMapper mediaMapper;

    public MediaManager(MediaDao mediaDao, ImageService imageService, FileService fileService, MediaMapper mediaMapper) {
        this.mediaDao = mediaDao;
        this.imageService = imageService;
        this.fileService = fileService;
        this.mediaMapper = mediaMapper;
    }

    @Override
    public MediaUploadResponseDto uploadMedia(MultipartFile file) {

        logger.info("Starting media upload process for file: {}", file.getOriginalFilename());

        Media savedMedia;

        if (file.getContentType() != null &&
                file.getContentType().startsWith("image/")) {

            savedMedia = imageService.uploadImage(file);

        } else {

            savedMedia = fileService.uploadFile(file);
        }

        logger.info("Media upload process completed for file: {}. Media ID: {}", file.getOriginalFilename(), savedMedia.getId());

        return mediaMapper.toMediaUploadResponseDto(savedMedia);
    }

    @Override
    public MediaUploadResponseDto getMediaById(UUID id) {
        logger.info("Retrieving media with ID: {}", id);

        var media = mediaDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Media not found with ID: " + id));

        logger.info("Media retrieved successfully with ID: {}", id);

        return mediaMapper.toMediaUploadResponseDto(media);
    }
}
