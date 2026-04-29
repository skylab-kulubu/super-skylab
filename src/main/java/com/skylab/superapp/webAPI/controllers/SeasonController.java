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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/seasons")
@RequiredArgsConstructor
@Tag(name = "Sezon Yönetimi", description = "Akademik/Eğitim sezonlarının (Örn: 2024-2025 Sezonu) listelenmesi ve yönetimi")
public class SeasonController {

    private final SeasonService seasonService;

    @GetMapping
    @Operation(summary = "Tüm Sezonları Getir", description = "Sistemdeki tüm sezon kayıtlarını getirir.")
    public ResponseEntity<DataResult<List<SeasonDto>>> getAllSeasons() {
        var result = seasonService.getAllSeasons();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASONS_GET_ALL_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/active")
    @Operation(summary = "Aktif Sezonları Getir", description = "Sadece şu anda aktif olan sezonları listeler.")
    public ResponseEntity<DataResult<List<SeasonDto>>> getActiveSeasons() {
        var result = seasonService.getActiveSeasons();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASONS_GET_ALL_SUCCESS, HttpStatus.OK));
    }


    @GetMapping("/{id}")
    @Operation(summary = "Sezon Detayı Getir", description = "Belirtilen UUID'ye sahip sezonun bilgilerini getirir.")
    public ResponseEntity<DataResult<SeasonDto>> getSeasonById(@PathVariable UUID id) {
        var result = seasonService.getSeasonById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASON_GET_SUCCESS, HttpStatus.OK));
    }

    @PostMapping
    @Operation(summary = "Yeni Sezon Ekle", description = "Sisteme yeni bir sezon (Örn: 2025-2026) ekler.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sezon başarıyla oluşturuldu."),
            @ApiResponse(responseCode = "400", description = "Sezon adı zaten mevcut veya tarihler hatalı.", content = @Content)
    })
    public ResponseEntity<DataResult<SeasonDto>> addSeason(@RequestBody CreateSeasonRequest request) {
        var result = seasonService.addSeason(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASON_CREATED_SUCCESSFULLY, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Sezon Güncelle", description = "Belirtilen sezonun isim, tarih veya aktiflik durumunu günceller.")
    public ResponseEntity<DataResult<SeasonDto>> updateSeason(@PathVariable UUID id, @RequestBody UpdateSeasonRequest request) {
        var result = seasonService.updateSeason(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASON_UPDATED_SUCCESSFULLY, HttpStatus.OK));
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Sezon Sil", description = "Belirtilen sezonu kalıcı olarak siler.")
    public ResponseEntity<Result> deleteSeason(@PathVariable UUID id) {
        seasonService.deleteSeason(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(SeasonMessages.SEASON_DELETED_SUCCESSFULLY, HttpStatus.OK));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Sezon Adına Göre Getir", description = "Belirtilen isimle eşleşen sezonu getirir.")
    public ResponseEntity<DataResult<SeasonDto>> getSeasonByName(@PathVariable String name) {
        var result = seasonService.getSeasonByName(name);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SeasonMessages.SEASON_GET_SUCCESS, HttpStatus.OK));
    }

}