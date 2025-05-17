package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.PhotoService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.business.constants.PhotoMessages;
import com.skylab.superapp.core.results.*;
import com.skylab.superapp.dataAccess.PhotoDao;
import com.skylab.superapp.entities.DTOs.Photo.CreatePhotoDto;
import com.skylab.superapp.entities.DTOs.Photo.GetPhotoDto;
import com.skylab.superapp.entities.Photo;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoManager implements PhotoService {

    private PhotoDao photoDao;
    private UserService userService;

    public PhotoManager(PhotoDao photoDao, @Lazy UserService userService) {
        this.photoDao = photoDao;
        this.userService = userService;
    }

    @Override
    public DataResult<Integer> addPhoto(CreatePhotoDto createPhotoDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();

        if (createPhotoDto.getPhotoUrl() == null || createPhotoDto.getPhotoUrl().isEmpty()) {
            return new ErrorDataResult<>(PhotoMessages.PhotoUrlCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        if (createPhotoDto.getTenant() == null || createPhotoDto.getTenant().isEmpty()) {
            return new ErrorDataResult<>(PhotoMessages.TenantCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        var tenantCheck = userService.tenantCheck(createPhotoDto.getTenant(), username);
        if (!tenantCheck) {
            return new ErrorDataResult<>(PhotoMessages.UserNotAuthorized, HttpStatus.UNAUTHORIZED);
        }

        var photo = Photo.builder()
                .photoUrl(createPhotoDto.getPhotoUrl())
                .tenant(createPhotoDto.getTenant())
                .build();

        photoDao.save(photo);
        return new SuccessDataResult<>(photo.getId(),PhotoMessages.PhotoAddedSuccess, HttpStatus.CREATED);
    }

    @Override
    public Result deletePhoto(int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();

        var photo = photoDao.findById(id);
        if (photo == null) {
            return new ErrorResult(PhotoMessages.PhotoNotFound, HttpStatus.NOT_FOUND);
        }

        var tenantCheck = userService.tenantCheck(photo.getTenant(), username);
        if(!tenantCheck) {
            return new ErrorResult(PhotoMessages.UserNotAuthorized, HttpStatus.UNAUTHORIZED);
        }

        photoDao.delete(photo);
        return new SuccessResult(PhotoMessages.PhotoDeletedSuccess, HttpStatus.OK);
    }

    @Override
    public Result updatePhoto(GetPhotoDto getPhotoDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();

        var photo = photoDao.findById(getPhotoDto.getId());
        if (photo == null) {
            return new ErrorResult(PhotoMessages.PhotoNotFound, HttpStatus.NOT_FOUND);
        }
        if (getPhotoDto.getPhotoUrl() == null || getPhotoDto.getPhotoUrl().isEmpty()) {
            return new ErrorResult(PhotoMessages.PhotoUrlCannotBeNull, HttpStatus.BAD_REQUEST);
        }
        var tenantCheck = userService.tenantCheck(getPhotoDto.getTenant(), username);
        if (!tenantCheck) {
            return new ErrorResult(PhotoMessages.UserNotAuthorized, HttpStatus.UNAUTHORIZED);
        }

        photo.setPhotoUrl(getPhotoDto.getPhotoUrl().isEmpty() ? photo.getPhotoUrl() : getPhotoDto.getPhotoUrl());

        photoDao.save(photo);
        return new SuccessResult(PhotoMessages.PhotoUpdatedSuccess, HttpStatus.OK);

    }

    @Override
    public DataResult<Photo> getPhotoEntityById(int id) {
        var photo = photoDao.findById(id);
        if (photo == null) {
            return new ErrorDataResult<>(PhotoMessages.PhotoNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<>(photo, PhotoMessages.PhotoFoundSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetPhotoDto>> getAllPhotos() {
        var photos = photoDao.findAll();
        if (photos.isEmpty()) {
            return new ErrorDataResult<>(PhotoMessages.NoPhotosFound, HttpStatus.NOT_FOUND);
        }

        var returnPhotos = GetPhotoDto.buildListGetPhotoDto(photos);
        return new SuccessDataResult<>(returnPhotos, PhotoMessages.PhotosFoundSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<Photo>> getPhotosByIds(List<Integer> ids) {
        List<Photo> photos = photoDao.findAllById(ids);
        if (photos.isEmpty()) {
            return new ErrorDataResult<>(PhotoMessages.NoPhotosFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<>(photos, PhotoMessages.PhotosFoundSuccess, HttpStatus.OK);

    }
}
