package com.skylab.superapp.entities.DTOs.Image;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skylab.superapp.entities.ImageCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ImageDto {
    private UUID id;
    private String name;
    private String type;
    private String url;
    private ImageCategory category;
    private String eventTypeName;
}