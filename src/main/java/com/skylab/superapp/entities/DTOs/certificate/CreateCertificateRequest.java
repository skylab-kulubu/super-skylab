package com.skylab.superapp.entities.DTOs.certificate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCertificateRequest {
    private String name;
    private String description;
    private UUID eventId;
    private List<UUID> ownerIds;
    private String storedLink;
    private Integer nameboxCenterX;
    private Integer nameboxCenterY;
}
