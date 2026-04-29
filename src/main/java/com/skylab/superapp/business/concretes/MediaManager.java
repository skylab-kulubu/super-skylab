package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.FileService;
import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.business.abstracts.MediaService;
import com.skylab.superapp.core.constants.MediaMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.mappers.MediaMapper;
import com.skylab.superapp.core.utilities.security.MediaSecurityUtils;
import com.skylab.superapp.dataAccess.MediaDao;
import com.skylab.superapp.entities.DTOs.media.response.MediaUploadResponseDto;
import com.skylab.superapp.entities.Media;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaManager implements MediaService {

    private final MediaDao mediaDao;
    private final ImageService imageService;
    private final FileService fileService;
    private final MediaMapper mediaMapper;
    private final MediaSecurityUtils mediaSecurityUtils;

    private static final Set<String> EXTERNAL_SERVICES = Set.of("skyforms");

    @Override
    public MediaUploadResponseDto uploadMedia(MultipartFile file) {
        log.info("Initiating media upload process. FileName: {}, ContentType: {}", file.getOriginalFilename(), file.getContentType());

        mediaSecurityUtils.checkUpload();

        Media savedMedia;

        if (file.getContentType() != null && file.getContentType().startsWith("image/")) {
            savedMedia = imageService.uploadImage(file);
        } else {
            savedMedia = fileService.uploadFile(file);
        }

        if (isExternalService()) {
            log.info("Media uploaded by external service. Attaching media automatically. MediaId: {}", savedMedia.getId());
            attachMedia(savedMedia.getId());
        }

        log.info("Media upload process completed successfully. MediaId: {}, FileName: {}", savedMedia.getId(), file.getOriginalFilename());

        return mediaMapper.toMediaUploadResponseDto(savedMedia);
    }

    @Override
    public MediaUploadResponseDto getMediaById(UUID id) {
        log.debug("Retrieving media. MediaId: {}", id);

        var media = mediaDao.findById(id)
                .orElseThrow(() -> {
                    log.error("Media retrieval failed: Resource not found. MediaId: {}", id);
                    return new ResourceNotFoundException(MediaMessages.MEDIA_NOT_FOUND_WITH_ID);
                });

        return mediaMapper.toMediaUploadResponseDto(media);
    }

    public void attachImageMedia(UUID mediaId) {
        log.info("Initiating image media attachment. MediaId: {}", mediaId);

        Media media = mediaDao.findById(mediaId)
                .orElseThrow(() -> {
                    log.error("Image media attachment failed: Resource not found. MediaId: {}", mediaId);
                    return new ResourceNotFoundException(MediaMessages.MEDIA_NOT_FOUND_WITH_ID);
                });

        if (!media.getFileType().startsWith("image/")) {
            log.warn("Image media attachment failed: Media is not an image type. MediaId: {}, FileType: {}", mediaId, media.getFileType());
            throw new ValidationException(MediaMessages.MEDIA_IS_NOT_AN_IMAGE);
        }

        media.setAttached(true);
        mediaDao.save(media);

        log.info("Image media attached successfully. MediaId: {}", mediaId);
    }

    public void attachMedia(UUID mediaId) {
        log.info("Initiating media attachment. MediaId: {}", mediaId);

        Media media = mediaDao.findById(mediaId)
                .orElseThrow(() -> {
                    log.error("Media attachment failed: Resource not found. MediaId: {}", mediaId);
                    return new ResourceNotFoundException(MediaMessages.MEDIA_NOT_FOUND_WITH_ID);
                });

        media.setAttached(true);
        mediaDao.save(media);

        log.info("Media attached successfully. MediaId: {}", mediaId);
    }

    private boolean isExternalService() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken token)) return false;

        String azp = token.getToken().getClaimAsString("azp");
        return EXTERNAL_SERVICES.contains(azp);
    }
}