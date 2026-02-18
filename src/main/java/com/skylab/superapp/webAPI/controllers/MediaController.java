package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.MediaService;
import com.skylab.superapp.core.constants.MediaMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.entities.DTOs.media.response.MediaUploadResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }


    @PostMapping
    public ResponseEntity<DataResult<MediaUploadResponseDto>> uploadMedia(@RequestParam("file") MultipartFile file) {
        var result = mediaService.uploadMedia(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessDataResult<>(result,MediaMessages.MEDIA_UPLOAD_SUCCESS, HttpStatus.CREATED));
    }


    @GetMapping("/{id}")
    public ResponseEntity<DataResult<MediaUploadResponseDto>> getMediaById(@PathVariable UUID id) {
        var result = mediaService.getMediaById(id);
        return ResponseEntity.ok(new SuccessDataResult<>(result,MediaMessages.MEDIA_RETRIEVE_SUCCESS, HttpStatus.OK));
    }
}
