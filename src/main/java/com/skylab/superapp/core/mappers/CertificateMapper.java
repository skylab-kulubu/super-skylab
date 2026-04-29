package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Certificate;
import com.skylab.superapp.entities.DTOs.certificate.CertificateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CertificateMapper {

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventName", source = "event.name")
    CertificateDto toDto(Certificate certificate);

}
