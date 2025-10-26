package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.core.constants.ImageMessages;
import com.skylab.superapp.core.mappers.ImageMapper;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.Image.response.UploadImageResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;


    public ImageController(ImageService imageService, ImageMapper imageMapper) {
        this.imageService = imageService;
    }


    @PostMapping("/")
    public ResponseEntity<SuccessDataResult<UploadImageResponseDto>> uploadImage(@RequestParam("image") MultipartFile image){
        UploadImageResponseDto responseImage = imageService.uploadImageDto(image);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new SuccessDataResult<UploadImageResponseDto>(responseImage,
                        ImageMessages.UPLOAD_IMAGE_SUCCESS,
                        HttpStatus.CREATED
                ));

    }


    @DeleteMapping("/{imageId}")
    public ResponseEntity<SuccessResult> deleteImage(@PathVariable UUID imageId) {

        imageService.deleteImage(imageId);

        return ResponseEntity.ok(
                new SuccessResult(ImageMessages.DELETE_IMAGE_SUCCESS,
                        HttpStatus.OK
                ));
    }



}