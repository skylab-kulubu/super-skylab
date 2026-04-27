package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.SessionService;
import com.skylab.superapp.core.constants.SessionMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.entities.DTOs.sessions.CreateSessionRequest;
import com.skylab.superapp.entities.DTOs.sessions.SessionDto;
import com.skylab.superapp.entities.DTOs.sessions.UpdateSessionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Etkinlik Oturumu Yönetimi", description = "Etkinliklerin iç programını oluşturan konuşma, dinleti ve oturumların yönetimi.")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    @Operation(summary = "Tüm Oturumları Listele", description = "Sistemdeki mevcut tüm bağımsız oturumları listeler.")
    public ResponseEntity<DataResult<List<SessionDto>>> getAllSessions() {
        log.info("REST request to get all sessions");
        var result = sessionService.getAllSessions();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SessionMessages.SESSIONS_GET_ALL_SUCCESS, HttpStatus.OK));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('sessions.create', 'sessions.moderator')")
    @Operation(summary = "Oturum Oluştur", description = "Sisteme yeni bir oturum tanımlar. Konuşmacı görseli opsiyoneldir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Oturum başarıyla eklendi."),
            @ApiResponse(responseCode = "400", description = "Validasyon hatası veya tarihler arası uyumsuzluk.", content = @Content)
    })
    public ResponseEntity<DataResult<SessionDto>> addSession(@Parameter(description = "Oturum Verileri (JSON)") @RequestPart("data") @Valid CreateSessionRequest request) {
        log.info("REST request to add new session with title: {}", request.getTitle());
        var result = sessionService.addSession(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, SessionMessages.SESSION_CREATED_SUCCESSFULLY, HttpStatus.CREATED));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('sessions.update', 'sessions.moderator')")
    @Operation(summary = "Oturum Güncelle", description = "Var olan bir oturumun spesifik verilerini günceller.")
    public ResponseEntity<DataResult<SessionDto>> updateSession(
            @Parameter(description = "Oturum UUID") @PathVariable UUID id,
            @RequestBody @Valid UpdateSessionRequest request) {

        log.info("REST request to update session with id: {}", id);
        var result = sessionService.updateSession(id, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, SessionMessages.SESSION_UPDATED_SUCCESFULLY, HttpStatus.OK));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('sessions.delete', 'sessions.moderator')")
    @Operation(summary = "Oturum Sil", description = "Oturum kaydını sistemden kalıcı olarak temizler.")
    public ResponseEntity<DataResult<Void>> deleteSession(@Parameter(description = "Oturum UUID") @PathVariable UUID id) {
        log.info("REST request to delete session with id: {}", id);
        sessionService.deleteSession(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(null, SessionMessages.SESSION_DELETED_SUCCESSFULLY, HttpStatus.OK));
    }
}