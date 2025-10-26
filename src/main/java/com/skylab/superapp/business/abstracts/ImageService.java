package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.Image.response.UploadImageResponseDto;
import com.skylab.superapp.entities.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ImageService {

    Image uploadImage(MultipartFile image);

    UploadImageResponseDto uploadImageDto(MultipartFile image);

    void deleteImage(UUID imageId);

    List<Image> getImagesByIds(List<UUID> imageIds);

}
