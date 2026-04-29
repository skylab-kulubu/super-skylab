package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.CertificateService;
import com.skylab.superapp.business.abstracts.EventService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.CertificateMessages;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.mappers.CertificateMapper;
import com.skylab.superapp.core.utilities.security.CertificateSecurityUtils;
import com.skylab.superapp.dataAccess.CertificateDao;
import com.skylab.superapp.entities.Certificate;
import com.skylab.superapp.entities.DTOs.certificate.CertificateDto;
import com.skylab.superapp.entities.DTOs.certificate.CreateCertificateRequest;
import com.skylab.superapp.entities.DTOs.certificate.UpdateCertificateRequest;
import com.skylab.superapp.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateManager implements CertificateService {

    private final CertificateDao certificateDao;
    private final CertificateMapper certificateMapper;
    private final EventService eventService;
    private final UserService userService;
    private final CertificateSecurityUtils certificateSecurityUtils;


    @Override
    @Transactional
    public CertificateDto createCertificate(CreateCertificateRequest request) {
        log.info("Initiating certificate creation. EventId: {}", request.getEventId());

        var event = eventService.getEventEntityById(request.getEventId());
        certificateSecurityUtils.checkCreate(event.getType().getName());

        List<User> owners = request.getOwnerIds() != null
                ? request.getOwnerIds().stream()
                .map(userService::getUserEntityById)
                .collect(Collectors.toList())
                : List.of();

        Certificate certificate = new Certificate();
        certificate.setName(request.getName());
        certificate.setDescription(request.getDescription());
        certificate.setEvent(event);
        certificate.setOwners(owners);
        certificate.setStoredLink(request.getStoredLink());
        certificate.setNameboxCenterX(request.getNameboxCenterX());
        certificate.setNameboxCenterY(request.getNameboxCenterY());

        var saved = certificateDao.save(certificate);
        log.info("Certificate created successfully. CertificateId: {}", saved.getId());

        return certificateMapper.toDto(saved);

    }

    @Override
    @Transactional
    public CertificateDto updateCertificate(UUID id, UpdateCertificateRequest request) {
        log.info("Initiating certificate update. CertificateId: {}", id);

        var certificate = getCertificateEntityById(id);
        certificateSecurityUtils.checkUpdate(certificate.getEvent().getType().getName());

        if (request.getName() != null) certificate.setName(request.getName());
        if (request.getDescription() != null) certificate.setDescription(request.getDescription());
        if (request.getStoredLink() != null) certificate.setStoredLink(request.getStoredLink());
        if (request.getNameboxCenterX() != null) certificate.setNameboxCenterX(request.getNameboxCenterX());
        if (request.getNameboxCenterY() != null) certificate.setNameboxCenterY(request.getNameboxCenterY());

        var saved = certificateDao.save(certificate);
        log.info("Certificate updated successfully. CertificateId: {}", id);

        return certificateMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteCertificate(UUID id) {
        log.info("Initiating certificate deletion. CertificateId: {}", id);


        var certificate = getCertificateEntityById(id);
        certificateSecurityUtils.checkDelete(certificate.getEvent().getType().getName());


        certificateDao.delete(certificate);
        log.info("Certificate deleted successfully. CertificateId: {}", id);
    }

    @Override
    public CertificateDto getCertificateById(UUID id) {
        log.debug("Retrieving certificate. CertificateId: {}", id);

        certificateSecurityUtils.checkRead();

        return certificateMapper.toDto(getCertificateEntityById(id));
    }

    @Override
    public List<CertificateDto> getCertificatesByEventId(UUID eventId) {
        log.debug("Retrieving certificates by event. EventId: {}", eventId);

        certificateSecurityUtils.checkRead();


        var event = eventService.getEventEntityById(eventId);
        return certificateDao.findAllByEvent(event).stream()
                .map(certificateMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CertificateDto> getCertificatesByUserId(UUID userId) {
        log.debug("Retrieving certificates by user. UserId: {}", userId);

        certificateSecurityUtils.checkRead();

        return certificateDao.findAllByOwners_Id(userId).stream()
                .map(certificateMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CertificateDto> getMyCertificates() {
        log.debug("Retrieving certificates for authenticated user.");

        certificateSecurityUtils.checkReadMe();

        var currentUser = userService.getAuthenticatedUserEntity();
        return getCertificatesByUserId(currentUser.getId());
    }

    private Certificate getCertificateEntityById(UUID id) {
        return certificateDao.findById(id).orElseThrow(() -> {
            log.error("Certificate retrieval failed: Resource not found. CertificateId: {}", id);
            return new ResourceNotFoundException(CertificateMessages.CERTIFICATE_NOT_FOUND);
        });
    }
}
