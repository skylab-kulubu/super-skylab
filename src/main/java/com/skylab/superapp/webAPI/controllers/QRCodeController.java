package com.skylab.superapp.webAPI.controllers;


import com.skylab.superapp.core.utilities.qr.QRCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api/qr-codes")
@RequiredArgsConstructor
@Tag(name = "QR Kod Servisi", description = "Dinamik veri veya URL'ler için anlık QR kod üretimi")
public class QRCodeController {

    private final QRCodeService qrCodeService;


    @GetMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Standart QR Kod Üret", description = "Verilen metin/URL değerinden standart bir QR kod resmi (PNG) oluşturur.")
    public ResponseEntity<byte[]> generateQRCode(
            @Parameter(description = "QR kod içine gömülecek veri (Metin veya URL)") @RequestParam String data,
            @Parameter(description = "Resmin genişliği (px)") @RequestParam int width,
            @Parameter(description = "Resmin yüksekliği (px)") @RequestParam int height) throws IOException {

        byte[] qrCode = qrCodeService.generateQRCode(data, width, height);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }

    @GetMapping(value = "/generate-with-logo", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Logolu QR Kod Üret", description = "Verilen metin/URL değerinden, ortasında SKY LAB logosu bulunan bir QR kod resmi (PNG) oluşturur.")
    public ResponseEntity<byte[]> generateQRCodeWithLogo(
            @Parameter(description = "QR kod içine gömülecek veri (Metin veya URL)") @RequestParam String data,
            @Parameter(description = "Resmin genişliği (px)") @RequestParam int width,
            @Parameter(description = "Resmin yüksekliği (px)") @RequestParam int height,
            @Parameter(description = "Logonun boyutu (Varsayılan: 50px)") @RequestParam(defaultValue = "50") int logoSize) throws IOException {

        byte[] qrCode = qrCodeService.generateQRCodeWithLogo(data, width, height, "sky-lab.png", logoSize);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }


}
