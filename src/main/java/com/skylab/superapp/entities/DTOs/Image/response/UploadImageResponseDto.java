package com.skylab.superapp.entities.DTOs.Image.response;

import lombok.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UploadImageResponseDto {

    private UUID id;

    private String fileName;

    private String fileType;

    private String fileUrl;

    private Long fileSize;

}
