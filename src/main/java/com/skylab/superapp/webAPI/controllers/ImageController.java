package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.business.constants.ImageMessages;
import com.skylab.superapp.core.results.*;
import com.skylab.superapp.entities.DTOs.Image.GetImageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    @Value("${api.url}")
    private String API_URL;

    @Value("${image.get.url}")
    private String IMAGE_GET_URL;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/addImage")
    public ResponseEntity<?> addImage(@RequestParam("image") Optional<MultipartFile> image) {
        if (!image.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorDataResult<>(ImageMessages.imageCannotBeNull, HttpStatus.BAD_REQUEST));
        }
        var result = imageService.addImage(image.get());
        if (!result.isSuccess()) {
            return ResponseEntity.status(result.getHttpStatus()).body(result);
        }

        GetImageDto responseImageDto = new GetImageDto(result.getData());
        responseImageDto.setImageUrl(API_URL + IMAGE_GET_URL + result.getData().getUrl());

        return ResponseEntity.status(result.getHttpStatus())
                .body(new SuccessDataResult<>(responseImageDto, result.getMessage(), result.getHttpStatus()));
    }

    @GetMapping("/getImageByUrl/{url}")
    public ResponseEntity<byte[]> getImageByUrl(@PathVariable Optional<String> url) {
        if (!url.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

        var imageResult = imageService.getImageByUrl(url.get());
        if (imageResult.isSuccess()) {
            return ResponseEntity.status(imageResult.getHttpStatus())
                    .contentType(MediaType.valueOf(imageResult.getData().getType()))
                    .body(imageResult.getData().getData());
        }
        return ResponseEntity.status(imageResult.getHttpStatus()).build();
    }

    @GetMapping("/getImageDetailsByUrl/{url}")
    public ResponseEntity<?> getImageDetailsByUrl(@PathVariable String url) {
        var imageResult = imageService.getImageByUrl(url);
        if (!imageResult.isSuccess()) {
            return ResponseEntity.status(imageResult.getHttpStatus()).body(imageResult);
        }

        GetImageDto responseImageDto = new GetImageDto(imageResult.getData());
        responseImageDto.setImageUrl(API_URL + IMAGE_GET_URL + imageResult.getData().getUrl());

        return ResponseEntity.status(imageResult.getHttpStatus())
                .body(new SuccessDataResult<>(responseImageDto, imageResult.getMessage(), imageResult.getHttpStatus()));
    }

    @DeleteMapping("/deleteImageById/{id}")
    public ResponseEntity<?> deleteImageById(@PathVariable int id) {
        var result = imageService.deleteImage(id);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }
}