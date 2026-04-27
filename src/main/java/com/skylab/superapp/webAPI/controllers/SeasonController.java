package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.SeasonService;
import com.skylab.superapp.core.constants.SeasonMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.season.CreateSeasonRequest;
import com.skylab.superapp.entities.DTOs.season.SeasonDto;
import com.skylab.superapp.entities.DTOs.season.UpdateSeasonRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/seasons")
@RequiredArgsConstructor
@Tag(name = "Sezon Yönetimi", description = "Akademik/Eğitim sezonlarının (Örn: 2024-2025 Sezonu) listelenmesi ve yönetimi")
public class SeasonController {

    private final SeasonService seasonService;

    @GetMapping
    @Operation(summary = "Tüm Sezonları Getir", description = "Sistemdeki tüm sezon kayıtlarını getirir.")
    public ResponseEntity<DataResult<List<SeasonDto>>> getAllSeasons() {
        log.info("REST request to get all seasons");
        var result = seasonService.getAllSeasons();

        if (result.isEmpty()) {
            log.debug("No seasons found, returning NO_CONTENT");
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new SuccessDataResult<>(SeasonMessages.SEASONS_NO_CONTENT, HttpStatus.NO_CONTENT));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASONS_GET_ALL_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/active")
    @Operation(summary = "Aktif Sezonları Getir", description = "Sadece şu anda aktif olan sezonları listeler.")
    public ResponseEntity<DataResult<List<SeasonDto>>> getActiveSeasons() {
        log.info("REST request to get all active seasons");
        var result = seasonService.getActiveSeasons();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASONS_GET_ALL_SUCCESS, HttpStatus.OK));
    }


    @GetMapping("/{id}")
    @Operation(summary = "Sezon Detayı Getir", description = "Belirtilen UUID'ye sahip sezonun bilgilerini getirir.")
    public ResponseEntity<DataResult<SeasonDto>> getSeasonById(@PathVariable UUID id) {
        log.info("REST request to get season by id: {}", id);
        var result = seasonService.getSeasonById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASON_GET_SUCCESS, HttpStatus.OK));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('seasons.create', 'seasons.moderator')")
    @Operation(summary = "Yeni Sezon Ekle", description = "Sisteme yeni bir sezon (Örn: 2025-2026) ekler.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sezon başarıyla oluşturuldu."),
            @ApiResponse(responseCode = "400", description = "Sezon adı zaten mevcut veya tarihler hatalı.", content = @Content)
    })
    public ResponseEntity<DataResult<SeasonDto>> addSeason(@RequestBody CreateSeasonRequest request) {
        log.info("REST request to add new season with name: {}", request.getName());
        var result = seasonService.addSeason(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASON_CREATED_SUCCESSFULLY, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('seasons.update', 'seasons.moderator')")
    @Operation(summary = "Sezon Güncelle", description = "Belirtilen sezonun isim, tarih veya aktiflik durumunu günceller.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<DataResult<SeasonDto>> updateSeason(@PathVariable UUID id, @RequestBody UpdateSeasonRequest request) {
        log.info("REST request to update season with id: {}", id);
        var result = seasonService.updateSeason(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASON_UPDATED_SUCCESSFULLY, HttpStatus.OK));
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('seasons.delete', 'seasons.moderator')")
    @Operation(summary = "Sezon Sil", description = "Belirtilen sezonu kalıcı olarak siler.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Result> deleteSeason(@PathVariable UUID id) {
        log.info("REST request to delete season with id: {}", id);
        seasonService.deleteSeason(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(SeasonMessages.SEASON_DELETED_SUCCESSFULLY, HttpStatus.OK));
    }

}