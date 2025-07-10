package com.skylab.superapp.webAPI.controllers;


import com.skylab.superapp.core.utilities.qr.QRCodeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value= "/api/qrCodes" )
public class QRCodeController {

    private final QRCodeService qrCodeService;

    public QRCodeController(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }


    @GetMapping(value = "/generateQRCode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCode(
            @RequestParam String url,
            @RequestParam int width,
            @RequestParam int height) throws IOException {

        byte[] qrCode = qrCodeService.generateQRCode(url, width, height);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }

    @GetMapping(value = "/generateQRCodeWithLogo", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCodeWithLogo(
            @RequestParam String url,
            @RequestParam int width,
            @RequestParam int height,
            @RequestParam(defaultValue = "50") int logoSize) throws IOException {

        byte[] qrCode = qrCodeService.generateQRCodeWithLogo(url, width, height, "sky-lab.png", logoSize);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }


}
