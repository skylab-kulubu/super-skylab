package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.media.response.MediaUploadResponseDto;
import com.skylab.superapp.entities.Media;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface MediaService {
    MediaUploadResponseDto uploadMedia(MultipartFile file);

    MediaUploadResponseDto getMediaById(UUID id);

    void attachMedia(UUID mediaId);

    void attachImageMedia(UUID mediaId);

}
