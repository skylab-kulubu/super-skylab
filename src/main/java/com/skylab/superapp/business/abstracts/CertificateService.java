package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.DTOs.certificate.CertificateDto;
import com.skylab.superapp.entities.DTOs.certificate.CreateCertificateRequest;
import com.skylab.superapp.entities.DTOs.certificate.UpdateCertificateRequest;

import java.util.List;
import java.util.UUID;

public interface CertificateService {
    CertificateDto createCertificate(CreateCertificateRequest request);
    CertificateDto updateCertificate(UUID id, UpdateCertificateRequest request);
    void deleteCertificate(UUID id);
    CertificateDto getCertificateById(UUID id);
    List<CertificateDto> getCertificatesByEventId(UUID eventId);
    List<CertificateDto> getCertificatesByUserId(UUID userId);
    List<CertificateDto> getMyCertificates();
}
