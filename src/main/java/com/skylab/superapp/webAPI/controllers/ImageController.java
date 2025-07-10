package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.core.constants.ImageMessages;
import com.skylab.superapp.core.mappers.ImageMapper;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.Image.ImageDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;
    private final ImageMapper imageMapper;

    @Value("${api.url}")
    private String API_URL;

    @Value("${image.get.url}")
    private String IMAGE_GET_URL;

    public ImageController(ImageService imageService, ImageMapper imageMapper) {
        this.imageService = imageService;
        this.imageMapper = imageMapper;
    }

    @PostMapping("/addImage")
    public ResponseEntity<DataResult<ImageDto>> addImage(@RequestParam("image") MultipartFile image, HttpServletRequest request) {
        var result = imageService.addImage(image);
        var dto = imageMapper.toDto(result);
        dto.setUrl(API_URL + IMAGE_GET_URL + result.getUrl());


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(dto, ImageMessages.ADD_SUCCESS, HttpStatus.CREATED, request.getRequestURI()));
    }

    @GetMapping("/getImageByUrl/{url}")
    public ResponseEntity<byte[]> getImageByUrl(@PathVariable Optional<String> url) {
        if (url.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

        var imageResult = imageService.getImageByUrl(url.get());
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf(imageResult.getType()))
                    .body(imageResult.getData());
    }

    @GetMapping("/getImageDetailsByUrl/{url}")
    public ResponseEntity<?> getImageDetailsByUrl(@PathVariable String url, HttpServletRequest request) {
        var image = imageService.getImageByUrl(url);
        var dto = imageMapper.toDto(image);

        dto.setUrl(API_URL + IMAGE_GET_URL + dto.getUrl());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(dto, ImageMessages.GET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @DeleteMapping("/deleteImageById/{id}")
    public ResponseEntity<Result> deleteImageById(@PathVariable UUID id, HttpServletRequest request) {
        imageService.deleteImage(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(ImageMessages.DELETE_SUCCESS, HttpStatus.OK,request.getRequestURI()));
    }
}