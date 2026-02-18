package com.skylab.superapp.entities.DTOs.media.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MediaUploadResponseDto {

    private UUID id;

    private String name;

    private String type;

    private String url;

    private Long size;


}
