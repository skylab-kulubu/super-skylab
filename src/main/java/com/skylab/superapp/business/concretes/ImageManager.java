package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.results.*;
import com.skylab.superapp.dataAccess.ImageDao;
import com.skylab.superapp.entities.Image;
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

    public ImageManager(ImageDao imageDao, UserService userService) {
        this.imageDao = imageDao;
        this.userService = userService;
    }

    @Override
    public DataResult<Image> addImage(MultipartFile file) {
        if (file == null) {
            return new ErrorDataResult<>(ImageMessages.imageCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        var usernameResult = userService.getAuthenticatedUsername();
        if (!usernameResult.isSuccess()) {
            return new ErrorDataResult<>(usernameResult.getMessage(), usernameResult.getHttpStatus());
        }

        var userResult = userService.getUserEntityByUsername(usernameResult.getData());
        if (!userResult.isSuccess()) {
            return new ErrorDataResult<>(userResult.getMessage(), userResult.getHttpStatus());
        }

        try {
            Image imageToSave = Image.builder()
                    .type(file.getContentType())
                    .name(file.getOriginalFilename())
                    .data(file.getBytes())
                    .url(generateUrl())
                    .createdBy(userResult.getData())
                    .build();

            imageDao.save(imageToSave);
            return new SuccessDataResult<>(imageToSave, ImageMessages.imageAddSuccess, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ErrorDataResult<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public DataResult<List<Image>> getImages() {
        var result = imageDao.findAll();
        if (result.isEmpty()) {
            return new ErrorDataResult<>(ImageMessages.imageCannotBeFound, HttpStatus.NOT_FOUND);
        }
        return new SuccessDataResult<>(result, ImageMessages.imageGetSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<Image> getImageById(int id) {
        var result = imageDao.findById(id);
        if (result.isEmpty()) {
            return new ErrorDataResult<>(ImageMessages.imageCannotBeFound, HttpStatus.NOT_FOUND);
        }
        return new SuccessDataResult<>(result.get(), ImageMessages.imageGetSuccess, HttpStatus.OK);
    }

    @Override
    public Result deleteImage(int id) {
        var result = imageDao.findById(id);
        if (result.isEmpty()) {
            return new ErrorResult(ImageMessages.imageCannotBeFound, HttpStatus.NOT_FOUND);
        }
        imageDao.delete(result.get());
        return new SuccessResult(ImageMessages.imageDeleteSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<Image> getImageByUrl(String url) {
        var result = imageDao.findByUrl(url);
        if (result.isEmpty()) {
            return new ErrorDataResult<>(ImageMessages.imageCannotBeFound, HttpStatus.NOT_FOUND);
        }
        return new SuccessDataResult<>(result.get(), ImageMessages.imageGetSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<Image>> getImagesByIds(List<Integer> imageIds) {
        List<Image> images = imageDao.findAllById(imageIds);
        if (images.isEmpty()) {
            return new ErrorDataResult<>(ImageMessages.imageCannotBeFound, HttpStatus.NOT_FOUND);
        }
        return new SuccessDataResult<>(images, ImageMessages.imageGetSuccess, HttpStatus.OK);
    }

    private String generateUrl() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[128];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().encodeToString(randomBytes);
    }
}