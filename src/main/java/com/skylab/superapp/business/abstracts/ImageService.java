package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.Image.ImageDto;
import com.skylab.superapp.entities.Image;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ImageService {

    Image addImage(MultipartFile file, HttpServletRequest request);

    List<Image> getImages();

    Image getImageById(UUID id);

    void deleteImage(UUID id);

    Image getImageByUrl(String url);

    List<Image> getImagesByIds(List<UUID> imageIds);

    List<ImageDto> getAllImages();

}
