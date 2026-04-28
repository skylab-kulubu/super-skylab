package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.MediaService;
import com.skylab.superapp.core.constants.MediaMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.entities.DTOs.media.response.MediaUploadResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Tag(name = "Medya Yönetimi", description = "Sistem üzerinde dosya ve medya yükleme, okuma işlemleri.")
public class MediaController {

    private final MediaService mediaService;


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Medya Yükle", description = "Sisteme dosya (görsel, döküman vb.) yükler ve dönüş olarak erişim URL'i sağlar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Medya başarıyla yüklendi."),
            @ApiResponse(responseCode = "400", description = "Geçersiz dosya tipi veya boyutu.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Yetkisiz erişim.", content = @Content)
    })
    public ResponseEntity<DataResult<MediaUploadResponseDto>> uploadMedia(@Parameter(description = "Yüklenecek Dosya") @RequestPart("file") MultipartFile file) {
        var result = mediaService.uploadMedia(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessDataResult<>(result,MediaMessages.MEDIA_UPLOAD_SUCCESS, HttpStatus.CREATED));
    }


    @GetMapping("/{id}")
    @Operation(summary = "Medya Bilgilerini Getir", description = "Belirtilen UUID değerine sahip medyanın meta verilerini döner. Genel erişime açıktır.")
    public ResponseEntity<DataResult<MediaUploadResponseDto>> getMediaById(@Parameter(description = "Medya UUID") @PathVariable UUID id) {
        var result = mediaService.getMediaById(id);
        return ResponseEntity.ok(new SuccessDataResult<>(result,MediaMessages.MEDIA_RETRIEVE_SUCCESS, HttpStatus.OK));
    }
}
