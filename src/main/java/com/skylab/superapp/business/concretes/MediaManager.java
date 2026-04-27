package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.FileService;
import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.business.abstracts.MediaService;
import com.skylab.superapp.core.constants.MediaMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.mappers.MediaMapper;
import com.skylab.superapp.dataAccess.MediaDao;
import com.skylab.superapp.entities.DTOs.media.response.MediaUploadResponseDto;
import com.skylab.superapp.entities.Media;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@Service
public class MediaManager implements MediaService {

    private final Logger logger = LoggerFactory.getLogger(MediaManager.class);

    private final MediaDao mediaDao;

    private final ImageService imageService;

    private final FileService fileService;

    private final MediaMapper mediaMapper;

    private static final Set<String> EXTERNAL_SERVICES = Set.of(
            "skyforms" //skyforms web client as "skyforms"
    );

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

        if (isExternalService()) {
            logger.info("File is uploaded by external service. Attaching it!");
            attachMedia(savedMedia.getId());
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


    public void attachImageMedia(UUID mediaId) {
        Media media = mediaDao.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException(MediaMessages.MEDIA_NOT_FOUND_WITH_ID));

        if (!media.getFileType().startsWith("image/")) {
            throw new ValidationException(MediaMessages.MEDIA_IS_NOT_AN_IMAGE);
        }

        /*
        if (media.isAttached()) {
            throw new ValidationException(MediaMessages.MEDIA_ALREADY_ATTACHED);
        }
         */

        media.setAttached(true);
        mediaDao.save(media);
    }

    public void attachMedia(UUID mediaId) {
        Media media = mediaDao.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException(MediaMessages.MEDIA_NOT_FOUND_WITH_ID));

        /*
        if (media.isAttached()) {
            throw new ValidationException(MediaMessages.MEDIA_ALREADY_ATTACHED);
        }
         */

        media.setAttached(true);
        mediaDao.save(media);
    }


    private boolean isExternalService() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken token)) return false;
        String azp = token.getToken().getClaimAsString("azp");
        return EXTERNAL_SERVICES.contains(azp);
    }




}
