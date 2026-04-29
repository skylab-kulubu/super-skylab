package com.skylab.superapp.entities.DTOs.certificate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCertificateRequest {
    private String name;
    private String description;
    private String storedLink;
    private Integer nameboxCenterX;
    private Integer nameboxCenterY;
}
