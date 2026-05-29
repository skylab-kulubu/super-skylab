package com.skylab.superapp.entities.DTOs.certificate;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PatchCertificateRequest {
    private String name;
    private String description;
    private String storedLink;
    private Integer nameboxCenterX;
    private Integer nameboxCenterY;
}
