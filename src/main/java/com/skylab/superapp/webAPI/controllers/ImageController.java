package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.business.constants.ImageMessages;
import com.skylab.superapp.core.results.*;
import com.skylab.superapp.entities.DTOs.Image.ResponseImageDto;
import com.skylab.superapp.entities.User;
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
    public ResponseEntity<DataResult<ResponseImageDto>> addImage(@RequestParam("image") Optional<MultipartFile> image){
        var result = imageService.addImage(image.get());
        if (!result.isSuccess()){
            return ResponseEntity.status(result.getHttpStatus()).body(
                    new ErrorDataResult<>(result.getMessage(), result.getHttpStatus()));
        }


         var responseUser = User.builder()
                .id(result.getData().getCreatedBy().getId())
                .username(result.getData().getCreatedBy().getUsername())
                .build();

        ResponseImageDto responseImageDto = ResponseImageDto.builder()
                .id(result.getData().getId())
                .name(result.getData().getName())
                .type(result.getData().getType())
                .url(API_URL+IMAGE_GET_URL+result.getData().getUrl())
                .createdBy(responseUser)
                .build();


        return ResponseEntity.status(result.getHttpStatus()).body(
                new SuccessDataResult<>(responseImageDto, result.getMessage(), result.getHttpStatus())
        );

    }

    @GetMapping("/getImageByUrl/{url}")
    public ResponseEntity<byte[]> getImageByUrl(@PathVariable Optional<String> url) {

        if (!url.isPresent()){
            var error = new ErrorResult(ImageMessages.imageUrlCannotBeNull, HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(error.getHttpStatus()).build();
        }

        var imageResult = imageService.getImageByUrl(url.get());

        if(imageResult.isSuccess()) {
            return ResponseEntity.status(imageResult.getHttpStatus())
                    .contentType(MediaType.valueOf(imageResult.getData().getType()))
                    .body(imageResult.getData().getData());
        }else{
            return ResponseEntity.status(imageResult.getHttpStatus()).build();
        }

    }

    @GetMapping("/getImageDetailsByUrl/{url}")
    public ResponseEntity<DataResult<ResponseImageDto>> getImageDetailsByUrl(@PathVariable String url){
        var imageResult = imageService.getImageByUrl(url);

        if (!imageResult.isSuccess()){
            return ResponseEntity.status(imageResult.getHttpStatus()).body(
                    new ErrorDataResult<ResponseImageDto>(imageResult.getMessage(), imageResult.getHttpStatus())
            );
        }

        User responseUserDto = User.builder()
                .id(imageResult.getData().getCreatedBy().getId())
                .username(imageResult.getData().getCreatedBy().getUsername())
                .build();

        ResponseImageDto responseImageDto = ResponseImageDto.builder()
                .id(imageResult.getData().getId())
                .name(imageResult.getData().getName())
                .type(imageResult.getData().getType())
                .url(API_URL+IMAGE_GET_URL+imageResult.getData().getUrl())
                .createdBy(responseUserDto)
                .build();

        return ResponseEntity.status(imageResult.getHttpStatus()).body(
                new SuccessDataResult<>(responseImageDto, imageResult.getMessage(), imageResult.getHttpStatus())
        );
    }

    @DeleteMapping("/deleteImageById/{id}")
    public ResponseEntity<Result> deleteImageById(@PathVariable int id){
        var result = imageService.deleteImage(id);

        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }


}
