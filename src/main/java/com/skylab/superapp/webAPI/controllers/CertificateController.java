package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.CertificateService;
import com.skylab.superapp.core.constants.CertificateMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.certificate.CertificateDto;
import com.skylab.superapp.entities.DTOs.certificate.CreateCertificateRequest;
import com.skylab.superapp.entities.DTOs.certificate.UpdateCertificateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
@Tag(name = "Sertifika Yönetimi", description = "Etkinlik katılımcılarına verilen sertifikaların yönetimi.")
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/{id}")
    @Operation(summary = "Sertifika Detayını Getir")
    public ResponseEntity<DataResult<CertificateDto>> getCertificateById(
            @Parameter(description = "Sertifika UUID") @PathVariable UUID id) {
        var result = certificateService.getCertificateById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CertificateMessages.CERTIFICATE_GET_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Etkinliğe Ait Sertifikaları Listele")
    public ResponseEntity<DataResult<List<CertificateDto>>> getCertificatesByEventId(
            @Parameter(description = "Etkinlik UUID") @PathVariable UUID eventId) {
        var result = certificateService.getCertificatesByEventId(eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CertificateMessages.CERTIFICATE_GET_ALL_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Kullanıcıya Ait Sertifikaları Listele")
    public ResponseEntity<DataResult<List<CertificateDto>>> getCertificatesByUserId(
            @Parameter(description = "Kullanıcı UUID") @PathVariable UUID userId) {
        var result = certificateService.getCertificatesByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CertificateMessages.CERTIFICATE_GET_ALL_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/me")
    @Operation(summary = "Kendi Sertifikalarımı Getir")
    public ResponseEntity<DataResult<List<CertificateDto>>> getMyCertificates() {
        var result = certificateService.getMyCertificates();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CertificateMessages.CERTIFICATE_GET_ALL_SUCCESS, HttpStatus.OK));
    }

    @PostMapping
    @Operation(summary = "Sertifika Oluştur")
    public ResponseEntity<DataResult<CertificateDto>> createCertificate(
            @RequestBody CreateCertificateRequest request) {
        var result = certificateService.createCertificate(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessDataResult<>(result, CertificateMessages.CERTIFICATE_CREATED_SUCCESSFULLY, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Sertifika Güncelle")
    public ResponseEntity<DataResult<CertificateDto>> updateCertificate(
            @Parameter(description = "Sertifika UUID") @PathVariable UUID id,
            @RequestBody UpdateCertificateRequest request) {
        var result = certificateService.updateCertificate(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, CertificateMessages.CERTIFICATE_UPDATED_SUCCESSFULLY, HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Sertifika Sil")
    public ResponseEntity<Result> deleteCertificate(
            @Parameter(description = "Sertifika UUID") @PathVariable UUID id) {
        certificateService.deleteCertificate(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(CertificateMessages.CERTIFICATE_DELETED_SUCCESSFULLY, HttpStatus.OK));
    }
}