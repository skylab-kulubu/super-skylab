package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.CompetitorService;
import com.skylab.superapp.core.constants.CompetitorMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.Competitor.CompetitorDto;
import com.skylab.superapp.entities.DTOs.Competitor.CreateCompetitorRequest;
import com.skylab.superapp.entities.DTOs.Competitor.LeaderboardDto;
import com.skylab.superapp.entities.DTOs.Competitor.UpdateCompetitorRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/competitors")
@RequiredArgsConstructor
@Tag(name = "Yarışmacı Yönetimi", description = "Kullanıcıların yarışmalara katılımı, puanlama sistemi ve liderlik tablosu işlemleri.")
public class CompetitorController {

    private final CompetitorService competitorService;

    @PostMapping
    @PreAuthorize("hasAnyRole('competitors.create', 'competitors.moderator')")
    @Operation(summary = "Yarışmacı Ekle", description = "Belirtilen kullanıcıyı belirli bir etkinliğe yarışmacı olarak kaydeder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Yarışmacı başarıyla oluşturuldu."),
            @ApiResponse(responseCode = "400", description = "Kullanıcı zaten yarışmacı veya iş kuralı ihlali.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Yetkisiz erişim.", content = @Content)
    })
    public ResponseEntity<DataResult<CompetitorDto>> addCompetitor(@RequestBody CreateCompetitorRequest request) {
        log.info("REST request to add new competitor for user id: {} to event id: {}", request.getUserId(), request.getEventId());
        var result = competitorService.addCompetitor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_ADD_SUCCESS, HttpStatus.CREATED));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('competitors.list', 'competitors.moderator')")
    @Operation(summary = "Tüm Yarışmacıları Listele", description = "Sistemdeki tüm etkinliklere ait tüm yarışmacı kayıtlarını getirir.")
    public ResponseEntity<DataResult<List<CompetitorDto>>> getAllCompetitors() {
        log.info("REST request to get all competitors");
        var result = competitorService.getAllCompetitors();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('competitors.get', 'competitors.moderator')")
    @Operation(summary = "Yarışmacı Detayını Getir", description = "Belirtilen yarışmacı ID'sine ait detayları döner.")
    public ResponseEntity<DataResult<CompetitorDto>> getCompetitorById(@PathVariable UUID id) {
        log.info("REST request to get competitor by id: {}", id);
        var result = competitorService.getCompetitorById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('competitors.update', 'competitors.moderator')")
    @Operation(summary = "Yarışmacı Verisini Güncelle", description = "Yarışmacının puanlama veya kazanma durumunu günceller.")
    public ResponseEntity<DataResult<CompetitorDto>> updateCompetitor(@Parameter(description = "Yarışmacı UUID") @PathVariable UUID id, @RequestBody UpdateCompetitorRequest request) {
        log.info("REST request to update competitor with id: {}", id);
        var result = competitorService.updateCompetitor(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_ADD_SUCCESS, HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('competitors.delete', 'competitors.moderator')")
    @Operation(summary = "Yarışmacı Kaydını Sil", description = "Yarışmacı atamasını sistemden kalıcı olarak temizler.")
    public ResponseEntity<Result> deleteCompetitor(@Parameter(description = "Yarışmacı UUID") @PathVariable UUID id) {
        log.info("REST request to delete competitor with id: {}", id);
        competitorService.deleteCompetitor(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(CompetitorMessages.COMPETITOR_DELETE_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('competitors.me', 'competitors.moderator')")
    @Operation(summary = "Aktif Kullanıcının Yarışma Geçmişi", description = "Sisteme giriş yapmış olan kullanıcının katıldığı tüm yarışmaların bilgilerini getirir.")
    public ResponseEntity<DataResult<List<CompetitorDto>>> getMyCompetitors() {
        log.info("REST request to get competitors for current authenticated user");
        var result = competitorService.getMyCompetitors();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/leaderboard/type/{eventTypeName}")
    @Operation(summary = "Genel Liderlik Tablosu", description = "Belirtilen etkinlik türüne ait tüm zamanların liderlik sıralamasını getirir. Oturum kontrolü gerektirmez.")
    public ResponseEntity<DataResult<List<LeaderboardDto>>> getLeaderboard(@Parameter(description = "Etkinlik Türü Adı") @PathVariable String eventTypeName) {
        log.info("REST request to get global leaderboard for event type: {}", eventTypeName);
        List<LeaderboardDto> result = competitorService.getLeaderboardByEventType(eventTypeName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/leaderboard/season/{seasonId}/type/{eventTypeName}")
    @Operation(summary = "Sezonluk Liderlik Tablosu", description = "Belirtilen sezon ve etkinlik türüne ait dönemsel liderlik sıralamasını getirir. Oturum kontrolü gerektirmez.")
    public ResponseEntity<DataResult<List<LeaderboardDto>>> getSeasonLeaderboard(@Parameter(description = "Sezon UUID") @PathVariable UUID seasonId,
                                                                                 @Parameter(description = "Etkinlik Türü Adı") @PathVariable String eventTypeName) {
        log.info("REST request to get season leaderboard for season id: {} and event type: {}", seasonId, eventTypeName);
        List<LeaderboardDto> result = competitorService.getLeaderboardBySeasonAndEventType(seasonId, eventTypeName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CompetitorMessages.COMPETITOR_GET_SUCCESS, HttpStatus.OK));
    }
}