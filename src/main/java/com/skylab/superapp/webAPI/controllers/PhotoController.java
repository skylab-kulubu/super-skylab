package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.PhotoService;
import com.skylab.superapp.entities.DTOs.Photo.CreatePhotoDto;
import com.skylab.superapp.entities.DTOs.Photo.GetPhotoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping("/addPhoto")
    public ResponseEntity<?> addPhoto(@RequestBody CreatePhotoDto createPhotoDto) {
        var result = photoService.addPhoto(createPhotoDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/deletePhoto")
    public ResponseEntity<?> deletePhoto(@RequestParam int id) {
        var result = photoService.deletePhoto(id);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/updatePhoto")
    public ResponseEntity<?> updatePhoto(@RequestBody GetPhotoDto getPhotoDto) {
        var result = photoService.updatePhoto(getPhotoDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllPhotos() {
        var result = photoService.getAllPhotos();
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

}
