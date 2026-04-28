package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.AnnouncementService;
import com.skylab.superapp.core.constants.AnnouncementMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.Announcement.AnnouncementDto;
import com.skylab.superapp.entities.DTOs.Announcement.CreateAnnouncementRequestDto;
import com.skylab.superapp.entities.DTOs.Announcement.UpdateAnnouncementRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/announcements")
@Tag(name = "Duyuru Yönetimi", description = "Sistem genelindeki duyuruların oluşturulması, listelenmesi ve güncellenmesi işlemleri.")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Yeni Duyuru Ekle", description = "Sisteme yeni bir duyuru kaydı oluşturur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Duyuru başarıyla eklendi."),
            @ApiResponse(responseCode = "400", description = "Validasyon hatası veya eksik parametre.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Erişim reddedildi.", content = @Content)
    })
    public ResponseEntity<DataResult<AnnouncementDto>> addAnnouncement(
            @RequestBody @Valid CreateAnnouncementRequestDto createAnnouncementRequest) {
        var announcement = announcementService.addAnnouncement(createAnnouncementRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(announcement, AnnouncementMessages.ANNOUNCEMENT_ADD_SUCCESS, HttpStatus.CREATED));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Duyuru Sil", description = "Belirtilen duyuruyu sistemden kalıcı olarak siler.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Duyuru başarıyla silindi."),
            @ApiResponse(responseCode = "403", description = "Erişim reddedildi.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Silinmek istenen duyuru bulunamadı.", content = @Content)
    })
    public ResponseEntity<Result> deleteAnnouncement(@Parameter(description = "Duyuru UUID") @PathVariable UUID id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(AnnouncementMessages.ANNOUNCEMENT_DELETE_SUCCESS,
                        HttpStatus.OK));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Duyuru Güncelle", description = "Duyuruya ait verileri kısmi olarak günceller.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Duyuru başarıyla güncellendi."),
            @ApiResponse(responseCode = "404", description = "Güncellenmek istenen duyuru bulunamadı.", content = @Content)
    })
    public ResponseEntity<DataResult<AnnouncementDto>> updateAnnouncement(@Parameter(description = "Duyuru UUID") @PathVariable UUID id,
                                                                          @RequestBody UpdateAnnouncementRequest updateAnnouncementRequest) {
        var announcement = announcementService.updateAnnouncement(id, updateAnnouncementRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(announcement, AnnouncementMessages.ANNOUNCEMENT_UPDATE_SUCCESS,
                        HttpStatus.OK));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Duyuru Detayı Getir", description = "Belirtilen UUID'ye sahip duyurunun detaylarını listeler.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Duyuru detayı getirildi."),
            @ApiResponse(responseCode = "404", description = "Duyuru bulunamadı.", content = @Content)
    })
    public ResponseEntity<DataResult<AnnouncementDto>> getAnnouncementById(@Parameter(description = "Duyuru UUID") @PathVariable UUID id) {
        var announcement = announcementService.getAnnouncementById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(announcement, AnnouncementMessages.ANNOUNCEMENT_GET_SUCCESS, HttpStatus.OK));
    }

    @GetMapping
    @Operation(summary = "Tüm Duyuruları Getir", description = "Sistemdeki tüm duyuruları liste halinde döner.")
    public ResponseEntity<DataResult<List<AnnouncementDto>>> getAllAnnouncements() {
        var announcements = announcementService.getAllAnnouncements();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult(announcements, AnnouncementMessages.ANNOUNCEMENT_GET_ALL_SUCCESS,
                        HttpStatus.OK));
    }

    @GetMapping("/event-type/{eventTypeId}")
    @Operation(summary = "Etkinlik Türüne Göre Duyuruları Getir", description = "Sadece belirli bir etkinlik türüne atanmış duyuruları filtreler.")
    public ResponseEntity<DataResult<List<AnnouncementDto>>> getAllAnnouncementsByEventTypeId(@Parameter(description = "Etkinlik Türü UUID") @PathVariable UUID eventTypeId) {
        var result = announcementService.getAllAnnouncementsByEventTypeId(eventTypeId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult(result, AnnouncementMessages.ANNOUNCEMENT_GET_SUCCESS,
                        HttpStatus.OK));
    }
}