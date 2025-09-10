package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.exceptions.ImageCannotBeNullException;
import com.skylab.superapp.core.exceptions.ImageNotFoundException;
import com.skylab.superapp.core.mappers.ImageMapper;
import com.skylab.superapp.dataAccess.ImageDao;
import com.skylab.superapp.entities.DTOs.Image.ImageDto;
import com.skylab.superapp.entities.Image;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class ImageManager implements ImageService {

    private final ImageDao imageDao;
    private final UserService userService;
    private final ImageMapper imageMapper;

    public ImageManager(ImageDao imageDao,@Lazy UserService userService, ImageMapper imageMapper) {
        this.imageDao = imageDao;
        this.userService = userService;
        this.imageMapper = imageMapper;
    }

    @Override
    public Image addImage(MultipartFile file, HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            throw new ImageCannotBeNullException();
        }

        var userResult = userService.getAuthenticatedUserEntity(request);

        try {
            Image imageToSave = Image.builder()
                    .type(file.getContentType())
                    .name(file.getOriginalFilename())
                    .data(file.getBytes())
                    .url(generateUrl())
                    .createdBy(userResult)
                    .build();

            return imageDao.save(imageToSave);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Image> getImages() {
        return imageDao.findAll();
    }

    @Override
    public Image getImageById(UUID id) {
        return getImageEntity(id);
    }

    @Override
    public void deleteImage(UUID id) {
        var image = getImageEntity(id);
        imageDao.delete(image);
    }

    @Override
    public Image getImageByUrl(String url) {
        return imageDao.findByUrl(url).orElseThrow(ImageNotFoundException::new);
    }

    @Override
    public List<Image> getImagesByIds(List<UUID> imageIds) {
        return imageDao.findAllByIds(imageIds);
    }

    @Override
    public List<ImageDto> getAllImages() {
        var list = imageDao.findAll();
        return imageMapper.toDtoList(list);
    }

    private String generateUrl() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[128];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().encodeToString(randomBytes);
    }

    private Image getImageEntity(UUID id){
        return imageDao.findById(id).orElseThrow(ImageNotFoundException::new);
    }
}