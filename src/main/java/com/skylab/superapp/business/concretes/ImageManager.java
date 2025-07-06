package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.exceptions.ImageCannotBeNullException;
import com.skylab.superapp.core.exceptions.ImageNotFoundException;
import com.skylab.superapp.core.results.*;
import com.skylab.superapp.dataAccess.ImageDao;
import com.skylab.superapp.entities.Image;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class ImageManager implements ImageService {

    private final ImageDao imageDao;
    private final UserService userService;

    public ImageManager(ImageDao imageDao,@Lazy UserService userService) {
        this.imageDao = imageDao;
        this.userService = userService;
    }

    @Override
    public Image addImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageCannotBeNullException();
        }

        var userResult = userService.getAuthenticatedUser();

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
    public Image getImageById(int id) {
        return getImageEntity(id);
    }

    @Override
    public void deleteImage(int id) {
        var image = getImageEntity(id);
        imageDao.delete(image);
    }

    @Override
    public Image getImageByUrl(String url) {
        return imageDao.findByUrl(url).orElseThrow(ImageNotFoundException::new);
    }

    @Override
    public List<Image> getImagesByIds(List<Integer> imageIds) {
        return imageDao.findAllByIds(imageIds);
    }

    private String generateUrl() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[128];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().encodeToString(randomBytes);
    }

    private Image getImageEntity(int id){
        return imageDao.findById(id).orElseThrow(ImageNotFoundException::new);
    }
}